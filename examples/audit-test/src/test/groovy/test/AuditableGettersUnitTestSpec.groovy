package test

import com.fasterxml.jackson.annotation.JsonIncludeProperties
import tools.jackson.databind.json.JsonMapper
import grails.plugins.orm.auditable.AuditEventType
import grails.plugins.orm.auditable.ReflectionUtils
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.util.Holders
import spock.lang.Specification

class AuditableGettersUnitTestSpec extends Specification implements ControllerUnitTest<AuthorController>, DomainUnitTest<Author> {

    void 'auditable getters resolve default config before the plugin sets its application'() {
        given: 'a controller unit test has populated Holders without running the audit plugin'
        assert grailsApplication != null
        assert ReflectionUtils.application == null
        assert Holders.findApplication() != null
        Author author = new Author(name: 'Ada', age: 42L)

        expect:
        author.logClassName == Author.name
        author.logExcluded.containsAll(['version', 'lastUpdated', 'lastUpdatedBy'])
        author.logVerboseEvents.containsAll(AuditEventType.values())

        and: 'Jackson can read the audit getters while serializing the domain'
        String json = JsonMapper.builder()
                .addMixIn(Author, AuditGetterSerialization)
                .build()
                .writeValueAsString(author)
        json.contains('Ada')
        json.contains('test.Author')
        json.contains('version')
    }

    @JsonIncludeProperties(['name', 'logExcluded', 'logClassName', 'logVerboseEvents'])
    static class AuditGetterSerialization {}
}
