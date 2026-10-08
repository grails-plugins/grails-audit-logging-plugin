package test

import grails.plugins.orm.auditable.AuditLoggingConfigUtils
import grails.plugins.orm.auditable.ReflectionUtils
import grails.util.Holders
import spock.lang.Specification

class AuditConfigWithoutApplicationSpec extends Specification {

    void 'fails clearly when audit configuration is accessed with no Grails application'() {
        given:
        assert ReflectionUtils.application == null
        assert Holders.findApplication() == null
        AuditLoggingConfigUtils.resetAuditConfig()

        when:
        AuditLoggingConfigUtils.auditConfig

        then:
        IllegalStateException exception = thrown()
        exception.message == 'AuditLoggingGrailsPlugin/BeanRegistrar initialization must complete before accessing audit configuration'

        cleanup:
        AuditLoggingConfigUtils.resetAuditConfig()
    }
}
