Grails Audit Logging Plugin
===

[![Java CI](https://github.com/grails-plugins/grails-audit-logging-plugin/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/grails-plugins/grails-audit-logging-plugin/actions/workflows/gradle.yml)
![Grails 8 Compatible](https://img.shields.io/badge/Compatible-brightGreen?label=Grails%208&labelColor=grey)

This plugin was originally maintained by [symentis](https://github.com/symentis) and was gracefully donated to the grails developers to maintain.

## Description

The Audit Logging plugin for Grails adds generic event based Audit Logging to a Grails project.

For older Grails versions, see "Supported Grails Versions" below.

## Documentation

 * For Grails 8.0.x development, see the [Snapshot User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/snapshot/)
 * For Grails 7.0.x, see the [6.0.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/6.0.x/)
 * For 4.x documentation, see [4.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/4.0.x/plugin.html)
 * For 3.x documentation, see [3.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/3.0.x/plugin.html)
 * For 2.x documentation, see [2.x User Guide](https://grails-plugins.github.io/grails-audit-logging-plugin/2.0.x/plugin.html)

## Grails Versions

| Grails Version | Audit Logging Plugin Line |
| --- | --- |
| Grails 8.0.x | [7.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/7.0.x) |
| Grails 7.0.x | [6.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/6.0.x) |
| Grails 4.0.10+ | [5.0.x branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/5.0.x) |
| Grails up to 4.0.9 | [4.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/4.x_maintenance) |
| Grails 3.3.x | [3.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/3.x_maintenance) |
| Grails 3.0.x-3.2.x | [2.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/2.x_maintenance) |
| Grails 2.x | [1.x_maintenance branch](https://github.com/grails-plugins/grails-audit-logging-plugin/tree/1.x_maintenance) |

## Moving to Maven Central

This repositories new artifacts are currently moved to Maven Central, since Bintray shut down MAY/01/21. You can obtain the old artifacts from https://repo.grails.org.


## audit-quickstart

You need to perform "grails audit-quickstart \<package\> \<DomainClass\>" after installing this plugin's 2.0.x version or later.

With this, you get an auditlog domain class in your project which is fully under your control.
The domain name is registered in your application.groovy with key "grails.plugin.auditLog.auditDomainClassName".

Example:

```shell
grails audit-quickstart org.example.myproject MyAuditLogEvent
```

## Issue Management

See [GitHub Issues](https://github.com/grails-plugins/grails-audit-logging-plugin/issues "Issues")

## Pull Requests

Pull requests are highly appreciated and welcome!

Please add integration tests for new features to the audit-test application.

## Contributors

Special thanks to all the <a href="https://github.com/grails-plugins/grails-audit-logging-plugin/graphs/contributors">contributors</a> (in alphabetical order):

	Aaron Long
    Alan Wikie
	Aldrin
	Andrey Zhuchkov
	Ankur Tripathi
	Burt Beckwith
	bzamora33
	Danny Casady
	Dennie de Lange
	Dhiraj Mahapatro
	Elmar Kretzer
    Felix Scheinost
	Fernando Cambarieri
	Graeme Rocher
	Jeff Palmer
	Jorge Aguilera
	Juergen Baumann
	Madhava Jay
    Matt Long
	Matthew A Stewart
	Paul Taylor
    Robert Oschwald
	Sami Mäkelä
	Sebastien Arbogast
	Semyon Atamas
	Shawn Hartsock
	Tom Crossland

	Project lead: TBD

***

<a href="https://www.yourkit.com/java/profiler/index.jsp"><img src="https://www.yourkit.com/images/yklogo.png" alt="YourKit Java Profiler"/></a>

YourKit is kindly supporting Grails open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and
[YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).





