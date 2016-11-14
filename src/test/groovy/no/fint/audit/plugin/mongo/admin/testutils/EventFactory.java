package no.fint.audit.plugin.mongo.admin.testutils;

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import no.fint.event.model.Event;

public enum EventFactory {
    ;

    public static MongoAuditEvent create(String orgId, String source, String client, String verb) {

        Event event = new Event(orgId, source, verb, client);
        for (int i = 0; i < 10; i++) {
            event.addData(new String("Hello world " + (i + 1)));
        }
        MongoAuditEvent mongoAuditEvent = new MongoAuditEvent(event, true);
        return mongoAuditEvent;

    }
}
