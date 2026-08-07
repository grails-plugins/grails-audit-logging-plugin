package test

import grails.core.GrailsApplication
import grails.plugins.orm.auditable.AuditLoggingConfigUtils
import grails.plugins.orm.auditable.AuditLoggingGrailsPlugin
import grails.plugins.orm.auditable.resolvers.AuditRequestResolver
import grails.plugins.orm.auditable.resolvers.DefaultAuditRequestResolver
import grails.testing.mixin.integration.Integration
import org.springframework.core.env.MapPropertySource
import spock.lang.Specification

@Integration
class AuditLoggingStartupSpec extends Specification {
    GrailsApplication grailsApplication

    void 'initializes audit configuration and the default request resolver at startup'() {
        expect:
        grailsApplication.mainContext.getBean('auditRequestResolver', AuditRequestResolver) instanceof DefaultAuditRequestResolver
        AuditLoggingConfigUtils.auditConfig.defaultActor == 'SYS'
        AuditLoggingConfigUtils.auditConfig.stampEnabled
        AuditLoggingConfigUtils.auditConfig.verbose
    }

    void 'reloads audit configuration after a config change'() {
        given:
        def propertySources = grailsApplication.mainContext.environment.propertySources
        propertySources.addAfter('AuditConfig', new MapPropertySource('auditConfigReloadSpec', [
            'grails.plugin.auditLog.defaultActor': 'RELOADED'
        ]))

        when:
        new AuditLoggingGrailsPlugin().onConfigChange([:])

        then:
        AuditLoggingConfigUtils.auditConfig.defaultActor == 'RELOADED'

        cleanup:
        propertySources.remove('AuditConfig')
        propertySources.remove('auditConfigReloadSpec')
        AuditLoggingConfigUtils.resetAuditConfig()
        AuditLoggingConfigUtils.auditConfig
    }
}
