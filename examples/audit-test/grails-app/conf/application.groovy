grails {
    plugin {
        auditLog {
            verbose = true
            excluded = ['version', 'lastUpdated', 'lastUpdatedBy']
            logFullClassName = true
            failOnError = true
            mask = ['ssn']
            logIds = true
            defaultActor = 'SYS'
            useDatasource = 'second' // store in "second" datasource
            replacementPatterns = ["a.b": ""]
            truncateLength = 1000000
        }
    }
}

// Added by the Audit-Logging plugin:
grails.plugin.auditLog.auditDomainClassName = 'test.AuditTrail'
