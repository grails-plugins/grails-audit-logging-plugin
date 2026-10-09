package test

import grails.plugins.orm.auditable.AuditLogContext
import grails.testing.mixin.integration.Integration
import org.hibernate.Hibernate
import org.hibernate.Session
import org.hibernate.Transaction
import org.springframework.transaction.TransactionStatus
import spock.lang.Specification

@Integration
class AuditHibernate7Spec extends Specification {

    void setup() {
        Author.withNewTransaction {
            AuditLogContext.withoutAuditLog {
                Author.where {}.deleteAll()
                Publisher.where {}.deleteAll()
            }
            AuditTrail.where {}.deleteAll()
        }
        EntityInSecondDatastore.withNewTransaction {
            AuditLogContext.withoutAuditLog {
                EntityInSecondDatastore.where {}.deleteAll()
            }
        }
    }

    void "insert, update and delete are audited when the transaction commits"() {
        when:
        Long id = Author.withTransaction {
            new Author(name: 'Aaron', age: 37).save(flush: true, failOnError: true).id
        }
        Author.withTransaction {
            Author.get(id).age = 38
        }
        Author.withTransaction {
            Author.get(id).delete(flush: true)
        }

        then:
        auditRows() == [
            ['DELETE', 'age', '38', null],
            ['DELETE', 'name', 'Aaron', null],
            ['DELETE', 'publisher', null, null],
            ['INSERT', 'age', null, '37'],
            ['INSERT', 'name', null, 'Aaron'],
            ['INSERT', 'publisher', null, null],
            ['UPDATE', 'age', '37', '38'],
        ]
    }

    void "a rolled back transaction is not audited and does not stop auditing in the same session"() {
        when:
        List<Session> sessions = []
        List<Transaction> transactions = []
        Author.withNewSession {
            Author.withTransaction { TransactionStatus transactionStatus ->
                new Author(name: 'Rolled back', age: 1).save(flush: true, failOnError: true)
                Author.withSession { Session session ->
                    sessions << session
                    transactions << session.transaction
                }
                transactionStatus.setRollbackOnly()
            }
            Author.withTransaction {
                new Author(name: 'Committed', age: 2).save(flush: true, failOnError: true)
                Author.withSession { Session session ->
                    sessions << session
                    transactions << session.transaction
                }
            }
        }

        then: "the second transaction ran in the same session, with the same Hibernate transaction"
        sessions[0].is(sessions[1])
        transactions[0].is(transactions[1])

        and:
        auditRows() == [
            ['INSERT', 'age', null, '2'],
            ['INSERT', 'name', null, 'Committed'],
            ['INSERT', 'publisher', null, null],
        ]
    }

    void "a masked to-one association is logged as the mask without initializing it"() {
        given:
        Long newPublisherId = Author.withNewTransaction {
            AuditLogContext.withoutAuditLog {
                def publisher = new Publisher(code: 'ABC123', name: 'Random House').save(failOnError: true)
                new Author(name: 'Aaron', age: 37, publisher: publisher).save(flush: true, failOnError: true)
                new Publisher(code: 'XYZ789', name: 'Penguin').save(flush: true, failOnError: true).id
            }
        }

        when:
        List<Boolean> initialized = Author.withNewTransaction {
            def author = Author.findByName('Aaron')
            def oldPublisher = author.publisher
            def newPublisher = Publisher.load(newPublisherId)
            AuditLogContext.withConfig(mask: ['publisher']) {
                author.publisher = newPublisher
                author.save(flush: true, failOnError: true)
            }
            [Hibernate.isInitialized(oldPublisher), Hibernate.isInitialized(newPublisher)]
        }

        then:
        initialized == [false, false]
        auditRows() == [['UPDATE', 'publisher', '**********', '**********']]
    }

    void "a committed inner transaction is audited when the outer transaction rolls back"() {
        given:
        Author.withNewTransaction {
            AuditLogContext.withoutAuditLog {
                new Author(name: 'Aaron', age: 37).save(flush: true, failOnError: true)
                new Publisher(code: 'ABC123', name: 'Random House').save(flush: true, failOnError: true)
            }
        }

        when:
        Author.withNewTransaction { TransactionStatus transactionStatus ->
            Author.findByName('Aaron').age = 1
            Author.withSession { Session session ->
                session.flush()
            }
            Publisher.withNewTransaction {
                Publisher.findByCode('ABC123').name = 'Penguin'
            }
            transactionStatus.setRollbackOnly()
        }

        then:
        Author.withNewTransaction { Author.findByName('Aaron').age } == 37
        auditRows() == [['UPDATE', 'name', 'Random House', 'Penguin']]
    }

    void "nested transactions in two datastores: the outer transaction rolls back"() {
        when:
        Author.withNewTransaction { TransactionStatus transactionStatus ->
            EntityInSecondDatastore.withNewTransaction {
                new Author(name: 'Rolled back', age: 12).save(flush: true, failOnError: true)
                new EntityInSecondDatastore(name: 'Committed').save(flush: true, failOnError: true)
            }
            transactionStatus.setRollbackOnly()
        }

        then:
        Author.withNewTransaction { Author.findByName('Rolled back') } == null
        EntityInSecondDatastore.withNewTransaction { EntityInSecondDatastore.findByName('Committed') } != null
        auditedClassNames() == ['test.EntityInSecondDatastore']
    }

    void "nested transactions in two datastores: the inner transaction rolls back"() {
        when:
        Author.withNewTransaction {
            EntityInSecondDatastore.withNewTransaction { TransactionStatus transactionStatus ->
                new Author(name: 'Committed', age: 12).save(flush: true, failOnError: true)
                new EntityInSecondDatastore(name: 'Rolled back').save(flush: true, failOnError: true)
                transactionStatus.setRollbackOnly()
            }
        }

        then:
        Author.withNewTransaction { Author.findByName('Committed') } != null
        EntityInSecondDatastore.withNewTransaction { EntityInSecondDatastore.findByName('Rolled back') } == null
        auditedClassNames() == ['test.Author']
    }

    private static List<List<String>> auditRows() {
        AuditTrail.withNewTransaction {
            AuditTrail.list()
                .collect { [it.eventName, it.propertyName, it.oldValue, it.newValue] }
                .sort { it[0] + it[1] }
        }
    }

    private static List<String> auditedClassNames() {
        AuditTrail.withNewTransaction {
            AuditTrail.list()*.className.unique().sort()
        }
    }
}
