package no.fint.audit.plugin.mongo.admin.repository;

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEventGroup;
import no.fint.audit.plugin.mongo.admin.model.PageableAuditEventGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
public class AuditEventMongoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * // This should return the following mongo shell appliance:
     * db.mongoAuditEvent.aggregate([{$group: {_id: "$corrId"}}, {$group: {_id: 1, total: {$sum: 1}}}]);
     *
     * db.mongoAuditEvent.aggregate([
     *      {$group: {_id : "$corrId", currentEvent: {"$last": "$event.status"}, events: { $push: "$$ROOT"} }},
     *      {$sort: {"events.timestamp": -1} }, // Latest first
     *      {$skip: page * pageSize },
     *      {$limit: pageSize}
     * ])
     */
    public PageableAuditEventGroup getAllAuditEvents(long page, long pageSize) {
        // Get totals, page and pagesize
        PageableAuditEventGroup pageable = mongoTemplate.aggregate(
            newAggregation(
                group("corrId"),
                group("_id.corrId").count().as("totalItems")
            ), MongoAuditEvent.class, PageableAuditEventGroup.class).getMappedResults().get(0);

        return pageable
            .setPage(page)
            .setPageSize(pageSize)

            // Get data
            .setData(mongoTemplate.aggregate(
                newAggregation(
                    group("corrId")
                        .last("event.status").as("currentEvent")
                        .push("$$ROOT").as("events")
                        .count().as("totalItems"),
                    sort(Sort.Direction.DESC, "events.timestamp"),
                    skip(page - 1),
                    limit(page * pageSize)
                ).withOptions(newAggregationOptions().allowDiskUse(true).build()),
                    MongoAuditEvent.class,
                    MongoAuditEventGroup.class
            ).getMappedResults());
    }

    public List<MongoAuditEventGroup> search(String what, long page, long pageSize) {
        return mongoTemplate.aggregate(
            newAggregation(
                match( // TODO: Does not work. Need to find a better way (REF: http://stackoverflow.com/questions/6790819/searching-for-value-of-any-field-in-mongodb-without-explicitly-naming-it#answer-25315763)
                    where("corrId").regex(what).orOperator(
                    where("event.source").regex(what).orOperator(
                    where("event.orgId").regex(what)
                ))),
                group("corrId").last("event.status").as("currentEvent").push("$$ROOT").as("events"),
                sort(Sort.Direction.DESC, "events.timestamp"),
                skip(page - 1),
                limit(page * pageSize)
                // TODO: Project row total, page and pageSize into the query resultset - at root level
            ).withOptions(newAggregationOptions().allowDiskUse(true).build()),
                MongoAuditEvent.class,
                MongoAuditEventGroup.class
        ).getMappedResults();
    }

/* The following methods would be redundant if the search method works */
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
/* */

    @Profile(value = "test")
    public void save(MongoAuditEvent mongoAuditEvent) {
        mongoTemplate.save(mongoAuditEvent);
    }

    @Profile(value = "test")
    public void drop() {
        mongoTemplate.dropCollection(MongoAuditEvent.class);
    }
}
