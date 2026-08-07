$ErrorActionPreference = 'Stop'

$repositoryRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$wrapper = Join-Path $repositoryRoot 'gradlew.bat'

function Invoke-WrapperScenario {
    param(
        [AllowNull()]
        [string] $JavaHome
    )

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $env:ComSpec
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.WorkingDirectory = $repositoryRoot
    $startInfo.ArgumentList.Add('/d')
    $startInfo.ArgumentList.Add('/v:off')
    $startInfo.ArgumentList.Add('/c')
    $startInfo.ArgumentList.Add($wrapper)
    $startInfo.Environment.Clear()
    $startInfo.Environment['ComSpec'] = $env:ComSpec
    $startInfo.Environment['SystemRoot'] = $env:SystemRoot
    $startInfo.Environment['PATH'] = ''
    if ($null -ne $JavaHome) {
        $startInfo.Environment['JAVA_HOME'] = $JavaHome
    }

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    $null = $process.Start()
    $output = $process.StandardOutput.ReadToEnd() + $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    [pscustomobject]@{
        ExitCode = $process.ExitCode
        Output = $output
    }
}

function Assert-WrapperFailure {
    param(
        [string] $Name,
        [AllowNull()]
        [string] $JavaHome,
        [string] $ExpectedDiagnostic,
        [string] $UnexpectedDiagnostic
    )

    $result = Invoke-WrapperScenario -JavaHome $JavaHome
    if ($result.ExitCode -ne 1) {
        throw "$Name returned exit code $($result.ExitCode) instead of 1. Output: $($result.Output)"
    }

    $diagnosticCount = ([regex]::Matches($result.Output, [regex]::Escape($ExpectedDiagnostic))).Count
    if ($diagnosticCount -ne 1) {
        throw "$Name emitted the expected diagnostic $diagnosticCount times instead of once. Output: $($result.Output)"
    }

    foreach ($secondaryError in @($UnexpectedDiagnostic, 'is not recognized as an internal or external command', 'The syntax of the command is incorrect.')) {
        if ($result.Output.Contains($secondaryError)) {
            throw "$Name fell through to a secondary error: $secondaryError. Output: $($result.Output)"
        }
    }
}

Assert-WrapperFailure -Name 'Missing Java' -JavaHome $null `
    -ExpectedDiagnostic "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." `
    -UnexpectedDiagnostic 'ERROR: JAVA_HOME is set to an invalid directory:'

Assert-WrapperFailure -Name 'Invalid JAVA_HOME' -JavaHome 'C:\invalid-java-home' `
    -ExpectedDiagnostic 'ERROR: JAVA_HOME is set to an invalid directory: C:\invalid-java-home' `
    -UnexpectedDiagnostic "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
