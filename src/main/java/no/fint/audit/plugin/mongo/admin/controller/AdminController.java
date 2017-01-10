package no.fint.audit.plugin.mongo.admin.controller;


import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent;
import no.fint.audit.plugin.mongo.admin.repository.AuditEventMongoRepository;
import no.rogfk.hateoas.extension.HalPagedResources;
import no.rogfk.hateoas.extension.annotations.HalResource;
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

    @HalResource(pageSize = 100)
    @RequestMapping(method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getAllAuditEvents(@RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getAllAuditEvents(), page);
    }


    @HalResource(pageSize = 100)
    @RequestMapping(value = "/source/{source}", method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getAllSourceAuditEvents(@PathVariable String source, @RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getAllAuditEventsBySource(source), page);
    }

    @HalResource(pageSize = 100)
    @RequestMapping(value = "/corrid/{corrId}", method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getAllAuditEventsByCorrId(@PathVariable String corrId, @RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getAllAuditEventsByCorrId(corrId), page);
    }

    @HalResource(pageSize = 100)
    @RequestMapping(value = "org", method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getOrgAuditEvents(@RequestHeader("x-org-id") String orgId, @RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getOrgAuditEvents(orgId), page);
    }

    @HalResource(pageSize = 100)
    @RequestMapping(value = "org/source/{source}", method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getOrgAuditEventsBySource(@RequestHeader("x-org-id") String orgId,
                                                                        @PathVariable String source,
                                                                        @RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getOrgAuditEventsBySource(orgId, source), page);
    }

    @HalResource(pageSize = 100)
    @RequestMapping(value = "org/corrid/{corrId}", method = RequestMethod.GET)
    public HalPagedResources<MongoAuditEvent> getOrgAuditEventsByCorrId(@RequestHeader("x-org-id") String orgId,
                                                                        @PathVariable String corrId,
                                                                        @RequestParam(required = false) Integer page) {
        return new HalPagedResources<>(auditEventMongoRepository.getOrgAuditEventsByCorrId(orgId, corrId), page);
    }


}
