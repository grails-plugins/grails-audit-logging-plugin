grails {
    plugin {
        auditLog {
            verbose = true
            excluded = ['version', 'lastUpdated', 'lastUpdatedBy']
            logFullClassName = true
            failOnError = true
            auditDomainClassName = 'test.AuditTrail'
        }
    }
}
