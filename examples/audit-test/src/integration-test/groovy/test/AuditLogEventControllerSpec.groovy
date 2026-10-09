package test

import grails.core.GrailsApplication
import grails.plugins.orm.auditable.AuditLogContext
import grails.plugins.orm.auditable.AuditLoggingConfigUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.MapPropertySource
import spock.lang.Specification

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Integration
class AuditLogEventControllerSpec extends Specification {

    private static final String PROPERTY_SOURCE = 'auditLogEventControllerSpec'

    GrailsApplication grailsApplication

    @Value('${local.server.port}')
    Integer port

    String auditTrailId

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
        Author.withNewTransaction {
            new Author(name: 'Aaron', age: 37, famous: true).save(flush: true, failOnError: true)
        }
        auditTrailId = AuditTrail.withNewTransaction {
            AuditTrail.findByPropertyName('name').id
        }
    }

    void cleanup() {
        def propertySources = grailsApplication.mainContext.environment.propertySources
        if (propertySources.remove(PROPERTY_SOURCE)) {
            propertySources.remove('AuditConfig')
            AuditLoggingConfigUtils.resetAuditConfig()
            AuditLoggingConfigUtils.auditConfig
        }
    }

    void "the controller does not serve the audit log by default"() {
        when:
        HttpResponse<String> list = get('/auditLogEvent/list')
        HttpResponse<String> show = get("/auditLogEvent/show/${auditTrailId}")

        then:
        list.statusCode() == 404
        !list.body().contains('test.Author')

        show.statusCode() == 404
        !show.body().contains('Aaron')
    }

    void "the controller lists and shows the audit log when the application enables it"() {
        given:
        grailsApplication.mainContext.environment.propertySources.addAfter('AuditConfig',
            new MapPropertySource(PROPERTY_SOURCE, ['grails.plugin.auditLog.controllerEnabled': true]))
        AuditLoggingConfigUtils.resetAuditConfig()

        when:
        HttpResponse<String> list = get('/auditLogEvent/list')
        HttpResponse<String> show = get("/auditLogEvent/show/${auditTrailId}")

        then:
        list.statusCode() == 200
        list.body().contains('test.Author')

        show.statusCode() == 200
        show.body().contains('Aaron')
    }

    private HttpResponse<String> get(String path) {
        HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(URI.create("http://localhost:${port}${path}")).build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }
}
