package no.fint.audit.plugin.mongo.admin.controller;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.admin.model.PageableAuditEventGroup;
import no.fint.audit.plugin.mongo.admin.repository.AuditEventMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RequestMapping(value = "/audit/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AdminController {

    @Autowired
    AuditEventMongoRepository auditEventMongoRepository;

    @RequestMapping(method = RequestMethod.GET)
    public PageableAuditEventGroup getAllAuditEvents(@RequestHeader(required = false, name = "x-org-id") final String orgId, @RequestParam(required = false, defaultValue = "1") Long page, @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return auditEventMongoRepository.getAllAuditEvents(orgId, page, pageSize);
    }

    @RequestMapping(value = "/search/{what}", method = RequestMethod.GET)
    public PageableAuditEventGroup search(@RequestHeader(required = false, name = "x-org-id") final String orgId, @PathVariable String what, @RequestParam(required = false, defaultValue = "1") Long page, @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return auditEventMongoRepository.search(orgId, what, page, pageSize);
    }
}
