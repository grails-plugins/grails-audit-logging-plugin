package test

import grails.core.GrailsApplication
import grails.plugins.orm.auditable.AuditLogContext
import grails.plugins.orm.auditable.AuditLoggingConfigUtils
import grails.testing.mixin.integration.Integration
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.io.ByteArrayResource
import spock.lang.Specification

@Integration
class AuditEventTypeYamlConfigSpec extends Specification {

    private static final String PROPERTY_SOURCE = 'auditEventTypeYamlConfigSpec'

    // Loaded by the same loader as application.yml, so the event types arrive as names
    private static final String YAML = '''\
grails:
    plugin:
        auditLog:
            verbose: false
            verboseEvents:
                - UPDATE
            ignoreEvents:
                - DELETE
'''

    GrailsApplication grailsApplication

    void setup() {
        Author.withNewTransaction {
            AuditLogContext.withoutAuditLog {
                Book.where {}.deleteAll()
                Author.where {}.deleteAll()
            }
        }
        AuditTrail.withNewTransaction {
            AuditTrail.where {}.deleteAll()
        }

        def yaml = new YamlPropertySourceLoader().load(PROPERTY_SOURCE, new ByteArrayResource(YAML.bytes))
        grailsApplication.mainContext.environment.propertySources.addAfter('AuditConfig', yaml.first())
        AuditLoggingConfigUtils.resetAuditConfig()
    }

    void cleanup() {
        def propertySources = grailsApplication.mainContext.environment.propertySources
        propertySources.remove(PROPERTY_SOURCE)
        propertySources.remove('AuditConfig')
        AuditLoggingConfigUtils.resetAuditConfig()
        AuditLoggingConfigUtils.auditConfig
    }

    void "event type names from YAML select the verbose and the ignored events"() {
        expect: "the merged configuration holds the names as strings"
        AuditLoggingConfigUtils.auditConfig.verboseEvents == ['UPDATE']
        AuditLoggingConfigUtils.auditConfig.ignoreEvents == ['DELETE']

        when:
        Long id = Author.withNewTransaction {
            new Author(name: 'Aaron', age: 37, famous: true).save(flush: true, failOnError: true).id
        }
        Author.withNewTransaction {
            def author = Author.get(id)
            author.age = 38
            author.famous = false
        }
        Author.withNewTransaction {
            Author.get(id).delete(flush: true)
        }

        then: "one INSERT row, an UPDATE row for each changed property and no DELETE row"
        AuditTrail.withNewTransaction {
            AuditTrail.withCriteria { eq('className', 'test.Author') }
                .collect { [it.eventName, it.propertyName] }
                .sort { "${it[0]}${it[1]}" }
        } == [['INSERT', null], ['UPDATE', 'age'], ['UPDATE', 'famous']]
    }
}
