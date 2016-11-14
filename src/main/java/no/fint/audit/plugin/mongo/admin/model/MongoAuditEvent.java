package no.fint.audit.plugin.mongo.admin.model;

import no.fint.audit.model.AuditEvent;
import no.fint.event.model.Event;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
public class MongoAuditEvent extends AuditEvent {

    @Id
    private String id;

    public MongoAuditEvent(Event event, boolean clearData) {
        super(event, clearData);
        id = UUID.randomUUID().toString();
    }
}
