package no.fint.audit.plugin.mongo.admin.controller;


import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import no.fint.audit.plugin.mongo.admin.repository.AuditEventMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping(value = "/audit/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AdminController {

    @Autowired
    AuditEventMongoRepository auditEventMongoRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllAuditEvents() {
        return auditEventMongoRepository.getAllAuditEvents();
    }


    @RequestMapping(value = "/source/{source}", method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllSourceAuditEvents(@PathVariable String source) {
        return auditEventMongoRepository.getAllAuditEventsBySource(source);
    }

    @RequestMapping(value = "/corrid/{corrId}", method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllAuditEventsByCorrId(@PathVariable String corrId) {
        return auditEventMongoRepository.getAllAuditEventsByCorrId(corrId);
    }

    @RequestMapping(value = "org", method = RequestMethod.GET)
    public List<MongoAuditEvent> getOrgAuditEvents(@RequestHeader("x-org-id") String orgId) {
        return auditEventMongoRepository.getOrgAuditEvents(orgId);
    }

    @RequestMapping(value = "org/source/{source}", method = RequestMethod.GET)
    public List<MongoAuditEvent> getOrgAuditEventsBySource(@RequestHeader("x-org-id") String orgId, @PathVariable String source) {
        return auditEventMongoRepository.getOrgAuditEventsBySource(orgId, source);
    }

    @RequestMapping(value = "org/corrid/{corrId}", method = RequestMethod.GET)
    public List<MongoAuditEvent> getOrgAuditEventsByCorrId(@RequestHeader("x-org-id") String orgId, @PathVariable String corrId) {
        return auditEventMongoRepository.getOrgAuditEventsByCorrId(orgId, corrId);
    }


}
