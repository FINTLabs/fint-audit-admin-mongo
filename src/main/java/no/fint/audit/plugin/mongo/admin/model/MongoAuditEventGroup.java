package no.fint.audit.plugin.mongo.admin.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by oystein.amundsen on 26.01.2017.
 */
public class MongoAuditEventGroup implements Serializable {
    @Id
    private String            corrId;
    private String            currentEvent;
    private MongoAuditEvent[] events;

    public MongoAuditEventGroup(String corrId, String currentEvent, MongoAuditEvent[] events) {
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

    public String getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(String currentEvent) {
        this.currentEvent = currentEvent;
    }

    public String getCorrId() {
        return corrId;
    }

    public void setCorrId(String corrId) {
        this.corrId = corrId;
    }
}
