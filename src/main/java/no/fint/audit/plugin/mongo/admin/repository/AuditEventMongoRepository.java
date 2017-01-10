package no.fint.audit.plugin.mongo.admin.repository;

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditEventMongoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<MongoAuditEvent> getAllAuditEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

    public List<MongoAuditEvent> getAllAuditEventsBySource(String source) {
        Query query = new Query();
        query.addCriteria(Criteria.where("source").regex(source, "i"));
        return mongoTemplate.find(query, MongoAuditEvent.class);
    }

    public List<MongoAuditEvent> getAllAuditEventsByCorrId(String corrId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("corrId").is(corrId));
        return mongoTemplate.find(query, MongoAuditEvent.class);
    }

    public List<MongoAuditEvent> getOrgAuditEvents(String orgId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId));
        return mongoTemplate.find(query, MongoAuditEvent.class);
    }

    public List<MongoAuditEvent> getOrgAuditEventsByCorrId(String orgId, String corrId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId)).addCriteria(Criteria.where("corrId").is(corrId));
        return mongoTemplate.find(query, MongoAuditEvent.class);
    }

    public List<MongoAuditEvent> getOrgAuditEventsBySource(String orgId, String source) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId)).addCriteria(Criteria.where("source").regex(source, "i"));
        return mongoTemplate.find(query, MongoAuditEvent.class);
    }

    @Profile(value = "test")
    public void save(MongoAuditEvent mongoAuditEvent) {
        mongoTemplate.save(mongoAuditEvent);
    }

    @Profile(value = "test")
    public void drop() {
        mongoTemplate.dropCollection(MongoAuditEvent.class);
    }
}
