package no.fint.audit.plugin.mongo.admin.model

import no.fint.event.model.Event
import spock.lang.Specification


class MongoAuditEventSpec extends Specification {

    def "Create MongoAuditEvent object"() {
        when:
            Event event = new Event()
            MongoAuditEvent mongoAuditEvent = new MongoAuditEvent(event, true)
        then:
            mongoAuditEvent.id != null
            mongoAuditEvent.event != null
    }
}
