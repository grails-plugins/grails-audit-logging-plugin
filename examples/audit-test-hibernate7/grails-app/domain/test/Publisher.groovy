package test

import grails.plugins.orm.auditable.Auditable

class Publisher implements Auditable {
    String code
    String name

    static constraints = {
        code nullable: false
        name nullable: false
    }
}
