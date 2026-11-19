package test

import grails.plugins.orm.auditable.Auditable

class EntityInSecondDatastore implements Auditable {
    String name

    static mapping = {
        datasource 'second'
    }
}
