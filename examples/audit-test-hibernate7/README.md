This is a sample test project for audit-logging.

You can run the tests from the repository root by

    ./gradlew :examples:audit-test-hibernate7:check :examples:audit-test-hibernate7:integrationTest

In comparison to `examples/audit-test` this application uses Hibernate 7 (`grails-data-hibernate7`). It also leaves out
`grails-rest-transforms`, so it only starts when the plugin declares the modules that its controller needs at runtime.
