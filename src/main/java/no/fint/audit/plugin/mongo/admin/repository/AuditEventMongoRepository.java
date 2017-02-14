package no.fint.audit.plugin.mongo.admin.repository;

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEventGroup;
import no.fint.audit.plugin.mongo.admin.model.PageableAuditEventGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Repository
public class AuditEventMongoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * This should run the following mongo shell appliance:
     *
     *      // Find totals
     *      db.mongoAuditEvent.aggregate([
     *          {$match: {orgId: "[orgId]"}},  // Only applied if orgId is given
     *          {$group: {_id: "$corrId"}},
     *          {$group: {_id: "$_id.corrId", totalItems: {$sum: 1}}}
     *      ]);
     *
     *      // Query data
     *      db.mongoAuditEvent.aggregate([
     *          {$match: {orgId: "[orgId]"}},  // Only applied if orgId is given
     *          {$group: {_id : "$corrId", currentEvent: {"$last": "$event"}, events: { $push: "$$ROOT"} }},
     *          {$sort: {"corrId": -1} }, // Latest first
     *          {$skip: page * pageSize },
     *          {$limit: pageSize}
     *      ]);
     *
     * @param orgId     - Only given if request comes from an client tied to an organisation
     * @param page      - Current page
     * @param pageSize  - How many items we should return per page
     *
     * @return A PageableAuditEventGroup object containing paging information and data
     */
    public PageableAuditEventGroup getAllAuditEvents(String orgId, long page, long pageSize) {
        // Create pageable
        PageableAuditEventGroup pageable = createPageable(wrapOrgIdOperations(orgId), page, pageSize);

        // Create next aggregation set
        List<AggregationOperation> operations = wrapOrgIdOperations(orgId);
        operations.add(group("corrId")
            .last("event").as("currentEvent")
            .push("$$ROOT").as("events"));
        operations.add(sort(Sort.Direction.DESC, "corrId"));
        operations.add(skip((page - 1) * pageSize));
        operations.add(limit(pageSize));

        // Aggregate and return pageable
        return pageable.setData(
                mongoTemplate.aggregate(
                    newAggregation(
                        operations.toArray(new AggregationOperation[operations.size()])
                    ).withOptions(newAggregationOptions().allowDiskUse(true).build()),
                    MongoAuditEvent.class,
                    MongoAuditEventGroup.class
                ).getMappedResults()
            );
    }

    /**
     * Performs a query on the audit events. The query runs matches on `what` for the following columns:
     *   * corrId
     *   * source
     *   * orgId (If orgId is not given. Otherwise a static fulltext match is performed)
     *
     * This should run the following mongo shell appliance:
     *
     *      // Create matcher for cases where orgId is NOT given
     *      var matcher =
     *        {$match: {
     *          $or: [
     *            {"corrId": {$regex: "what", $options: "i"}},
     *            {"source": {$regex: "what", $options: "i"}},
     *            {"orgId" : {$regex: "what", $options: "i"}}
     *          ]
     *        }};
     *
     *      // Create matcher for cases where orgId IS given
     *      var matcher =
     *        {$match: {
     *          "orgId": "[orgId]",
     *          $and: [{
     *            $or: [
     *              {"corrId": {$regex: "what", $options: "i"}},
     *              {"source": {$regex: "what", $options: "i"}}
     *            ]
     *          }]
     *        }},

     *      // Find totals
     *      db.mongoAuditEvent.aggregate([
     *          matcher,
     *          {"$group": {"_id": "$corrId"}},
     *          {"$group": {"_id": "$_id.corrId", "totalItems": {"$sum": 1}}}
     *      ]);
     *
     *      // Query data
     *      db.mongoAuditEvent.aggregate([
     *          matcher,
     *          {$group: {_id : "$corrId", currentEvent: {"$last": "$event"}, events: { $push: "$$ROOT"} }},
     *          {$sort: {"corrId": -1} }, // Latest first
     *          {$skip: page * pageSize },
     *          {$limit: pageSize}
     *      ]);
     *
     * @param orgId     - Only given if request comes from an client tied to an organisation
     * @param what      - What to search for
     * @param page      - Current page
     * @param pageSize  - How many items we should return per page
     *
     * @return A PageableAuditEventGroup object containing paging information and data
     */
    public PageableAuditEventGroup search(String orgId, String what, long page, long pageSize) {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.where("corrId").regex(what, "i"));
        criterias.add(Criteria.where("source").regex(what, "i"));
        if (orgId == null) { // If given orgId is null, search on orgId should be possible
            criterias.add(Criteria.where("orgId").regex(what, "i"));
        }
        Criteria matchCriterias = new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));

        // Wrap around an orgId matcher if orgId is specified
        if (orgId != null) { matchCriterias = Criteria.where("orgId").is(orgId).andOperator(matchCriterias); }

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(match(matchCriterias));
        PageableAuditEventGroup pageable = createPageable(operations, page, pageSize);

        return pageable.setData(
                mongoTemplate.aggregate(
                    newAggregation(
                        match(matchCriterias),
                        group("corrId").last("event").as("currentEvent").push("$$ROOT").as("events"),
                        sort(Sort.Direction.DESC, "events.timestamp"),
                        skip(page - 1),
                        limit(page * pageSize)
                    ).withOptions(newAggregationOptions().allowDiskUse(true).build()),
                    MongoAuditEvent.class,
                    MongoAuditEventGroup.class
                ).getMappedResults()
            );
    }

    /*
     * Creates a PageableAuditEventGroup object containing information for
     * the number of available rows based on your applied list of
     * aggregation operations, the current page number and amount of
     * elements per page.
     */
    private PageableAuditEventGroup createPageable(List<AggregationOperation> operations, long page, long pageSize) {
        operations.add(group("corrId"));
        operations.add(group("_id.corrId").count().as("totalItems"));

        PageableAuditEventGroup pageable;
        try {
            pageable = mongoTemplate.aggregate(
                newAggregation(operations.toArray(new AggregationOperation[operations.size()])),
                MongoAuditEvent.class,
                PageableAuditEventGroup.class
            ).getMappedResults().get(0);
        } catch(IndexOutOfBoundsException ex) {
            pageable = new PageableAuditEventGroup();
        }

        return pageable
            .setPage(page)
            .setPageSize(pageSize);
    }

    /*
     * Creates a List of AggregationOperations, applying a match for orgId
     * if orgId is specified. Return an empty list if not.
     */
    private List<AggregationOperation> wrapOrgIdOperations(String orgId) {
        List<AggregationOperation> operations = new ArrayList<>();
        if (orgId != null) { operations.add(match(Criteria.where("orgId").is(orgId))); }
        return operations;
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
