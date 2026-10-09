package grails.plugins.orm.auditable

import groovy.transform.CompileStatic
import org.grails.datastore.mapping.engine.event.EventType

/**
 * Simple enum for audit events
 */
@CompileStatic
enum AuditEventType {
    INSERT, UPDATE, DELETE

    @Override
    String toString() {
        name()
    }

    static AuditEventType forEventType(EventType type) {
        switch(type) {
            case EventType.PostInsert:
                return INSERT
            case EventType.PreDelete:
                return DELETE
            case EventType.PreUpdate:
                return UPDATE
            default:
                throw new IllegalArgumentException("Unexpected event type $type")
        }
    }

    /**
     * Resolves configured event types. A list from application.yml holds the names as strings,
     * so a name is accepted as well as an AuditEventType.
     *
     * @param value the configured event types, or a single event type
     * @param key the configuration key, for the error message
     * @return the event types, empty if none are configured
     * @throws IllegalArgumentException if a name does not match an AuditEventType
     */
    static Set<AuditEventType> fromConfig(Object value, String key) {
        if (!value) {
            return Collections.<AuditEventType>emptySet()
        }
        Collection eventTypes = value instanceof Collection ? (Collection) value : [value]
        eventTypes.collect { Object eventType ->
            if (eventType instanceof AuditEventType) {
                return (AuditEventType) eventType
            }
            String name = String.valueOf(eventType)
            try {
                return AuditEventType.valueOf(name)
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown event type '$name' in $key, expected one of ${AuditEventType.values().toList()}", e)
            }
        } as Set<AuditEventType>
    }
}