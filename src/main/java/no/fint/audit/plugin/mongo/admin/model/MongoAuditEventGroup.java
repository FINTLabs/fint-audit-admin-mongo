package no.fint.audit.plugin.mongo.admin.model;

import no.fint.event.model.Event;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by oystein.amundsen on 26.01.2017.
 */
public class MongoAuditEventGroup implements Serializable {
    @Id
    private String            corrId;
    private Event             currentEvent;
    private MongoAuditEvent[] events;

    public MongoAuditEventGroup(String corrId, Event currentEvent, MongoAuditEvent[] events) {
        this.corrId = corrId;
        this.currentEvent = currentEvent;
        this.events = events;
    }

    public MongoAuditEvent[] getEvents() {
        return events;
    }

    public void setEvents(MongoAuditEvent[] events) {
        this.events = events;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public String getCorrId() {
        return corrId;
    }

    public void setCorrId(String corrId) {
        this.corrId = corrId;
    }
}
