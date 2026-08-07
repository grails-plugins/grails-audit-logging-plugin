# Grails Audit Logging Plugin

[![Java CI](https://github.com/grails-plugins/grails-audit-logging-plugin/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/grails-plugins/grails-audit-logging-plugin/actions/workflows/gradle.yml)
![Grails 8 Compatible](https://img.shields.io/badge/Compatible-brightGreen?label=Grails%208&labelColor=grey)

The Audit Logging plugin adds event-based audit logging to a Grails application. It was originally maintained by [symentis](https://github.com/symentis) and donated to the Grails community.

## Requirements

Plugin 7.x supports Grails 8 and Java 21 only. Use plugin 6.x for Grails 7 applications.

## Installation

Normal applications declare only the runtime plugin. While using the 7.0.0 snapshot, add the Grails restricted repository and dependency to `build.gradle`:

```groovy
repositories {
    maven { url = uri('https://repo.grails.org/grails/restricted') }
}

dependencies {
    implementation 'org.grails.plugins:audit-logging:7.0.0-SNAPSHOT'
}
```

When released 7.x artifacts are available, resolve them from Maven Central with the released version instead. Do not add `audit-logging-cli` to your application dependencies.

## What Changed in 7.x

The runtime plugin and its CLI companion are separate artifacts. The runtime JAR advertises `org.grails.plugins:audit-logging-cli` through `Grails-Cli-Artifact`, so Grails 8 provisions the companion automatically only when running CLI commands. This keeps CLI dependencies out of `bootRun`, `bootJar`, and `bootWar`.

Plugin 7.x also moves to the Grails 8 and Java 21 baseline. It is not compatible with Grails 7.

## First-Time Setup

For a new application, create an audit domain artifact once:

```shell
grails audit-quickstart org.example.audit AuditLogEvent
```

You can run the same command from the interactive shell:

```shell
./gradlew shell
grails> audit-quickstart org.example.audit AuditLogEvent
```

Quickstart generates an audit domain class and registers its fully qualified name under `grails.plugin.auditLog.auditDomainClassName`. Review the generated mappings and constraints, then update the database schema before using the application. Keep the required nullability constraints intact.

Don't use `runCommand` or a generated `auditQuickstart` Gradle task. `audit-quickstart` is a legacy script packaged in `META-INF/commands`, not an `ApplicationCommand`.

To audit a domain class, implement `Auditable`:

```groovy
import grails.plugins.orm.auditable.Auditable

class Book implements Auditable {
    String title
}
```

## Upgrading Existing Applications

When upgrading an application from plugin 6.x, keep your customized `AuditLogEvent` class and existing `grails.plugin.auditLog.*` configuration. Do not rerun quickstart: it does not overwrite the existing class and can append duplicate configuration. If you intentionally redesign the audit domain, update the class and configuration manually.

Move the application to Grails 8 and Java 21, change the runtime dependency to plugin 7.x, and refresh dependencies. The companion is still discovered from the runtime plugin, so no explicit CLI dependency is needed.

## CLI Companion Architecture

Grails discovers the CLI companion from the runtime plugin manifest. The companion is added to the CLI classpath only for direct `grails` commands or the interactive `shell`, not to the runtime application classpath.

This separation is intentional. It avoids pulling CLI-only dependencies into application startup and packaged application artifacts while preserving the `audit-quickstart` command.

## Troubleshooting

If `audit-quickstart` is unavailable:

1. Confirm that the application declares the runtime `audit-logging` dependency, not the CLI companion.
2. For snapshots, confirm that `https://repo.grails.org/grails/restricted` is configured.
3. Keep `grails.cliAutoProvision` enabled.
4. Refresh the CLI classpath with `./gradlew --refresh-dependencies shell`.
5. Inspect resolved CLI dependencies with `./gradlew dependencies --configuration grailsCli`.

## Documentation

- For Grails 8 and plugin 7.x development, see the [Snapshot User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/snapshot/).
- For Grails 7 and plugin 6.x, see the [6.0.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/6.0.x/).
- For older releases, see the [4.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/4.0.x/plugin.html), [3.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/3.0.x/plugin.html), and [2.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/2.0.x/plugin.html).

## Compatibility

| Grails Version | Audit Logging Plugin Line |
| --- | --- |
| Grails 8.0.x | [7.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/7.0.x) |
| Grails 7.0.x | [6.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/6.0.x) |
| Grails 4.0.x | [5.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/5.0.x) (recommended); [4.x_maintenance](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/4.x_maintenance) remains available through Grails 4.0.9 |
| Grails 3.3.x | [3.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/3.x_maintenance) |
| Grails 3.0.x-3.2.x | [2.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/2.x_maintenance) |
| Grails 2.x | [1.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/1.x_maintenance) |

## Issue Management

See [GitHub Issues](https://github.com/grails-plugins/grails-audit-logging-plugin/issues "Issues").

## Pull Requests

Pull requests are welcome. Please add integration tests for new features to the audit-test application.

## Contributors

Special thanks to all the <a href="https://github.com/grails-plugins/grails-audit-logging-plugin/graphs/contributors">contributors</a> (in alphabetical order):

- Aaron Long
- Alan Wikie
- Aldrin
- Andrey Zhuchkov
- Ankur Tripathi
- Burt Beckwith
- bzamora33
- Danny Casady
- Dennie de Lange
- Dhiraj Mahapatro
- Elmar Kretzer
- Felix Scheinost
- Fernando Cambarieri
- Graeme Rocher
- Jeff Palmer
- Jorge Aguilera
- Juergen Baumann
- Madhava Jay
- Matt Long
- Matthew A Stewart
- Paul Taylor
- Robert Oschwald
- Sami Mäkelä
- Sebastien Arbogast
- Semyon Atamas
- Shawn Hartsock
- Tom Crossland

Project lead: TBD.

---

<a href="https://www.yourkit.com/java/profiler/index.jsp"><img src="https://www.yourkit.com/images/yklogo.png" alt="YourKit Java Profiler"/></a>

YourKit supports Grails open source projects with its Java Profiler. YourKit, LLC creates tools for profiling Java and .NET applications. See [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).
