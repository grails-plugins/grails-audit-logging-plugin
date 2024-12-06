package test

import grails.plugins.orm.auditable.Auditable

class Tunnel implements Auditable {
    String name
    String description

    static constraints = {
        description maxSize:1000000000, nullable:true
    }
}
