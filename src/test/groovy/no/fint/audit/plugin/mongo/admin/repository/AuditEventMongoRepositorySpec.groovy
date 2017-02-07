package no.fint.audit.plugin.mongo.admin.repository

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEventGroup
import no.fint.audit.plugin.mongo.admin.testutils.EventFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest
class AuditEventMongoRepositorySpec extends Specification {

    @Autowired
    private AuditEventMongoRepository auditEventMongoRepository

    private MongoAuditEvent mongoAuditEvent

    def setup() {
        mongoAuditEvent = EventFactory.create("rogfk.no", "Arbeidstaker", "VFS", "GET_ALL_EMPLOYEES")
        auditEventMongoRepository.save(mongoAuditEvent)
        auditEventMongoRepository.save(EventFactory.create("vaf.no", "Arbeidstaker", "BizTalk", "GET_ALL_EMPLOYEES"))
        auditEventMongoRepository.save(EventFactory.create("rogfk.no", "Organisasjon", "VFS", "GET_ORG"))
        auditEventMongoRepository.save(EventFactory.create("rogfk.no", "Kodeverk", "IDM", "GET_EMP_CODES"))
        auditEventMongoRepository.save(EventFactory.create("vaf.no", "Kodeverk", "VFS", "GET_ABS"))

    }

    def cleanup() {
        auditEventMongoRepository.drop()
    }

    def "Get All Audit Events"() {
        when:
        List<MongoAuditEventGroup> events = auditEventMongoRepository.getAllAuditEvents(1, 10)

        then:
        events.size() == 5
    }

    def "Get All Audit Events By Source"() {
        when:
        List<MongoAuditEvent> events = auditEventMongoRepository.getAllAuditEventsBySource("Arbeidstaker")

        then:
        events.size() == 2
    }

    def "Get All Audit Events By CorrId"() {
        when:
        List<MongoAuditEvent> events = auditEventMongoRepository.getAllAuditEventsByCorrId(mongoAuditEvent.corrId)

        then:
        events.size() == 1
    }

    def "Get Org Audit Events"() {
        when:
        List<MongoAuditEvent> events = auditEventMongoRepository.getOrgAuditEvents("vaf.no")

        then:
        events.size() == 2
    }

    def "Get Org Audit Events By CorrId"() {
        when:
        List<MongoAuditEvent> events = auditEventMongoRepository.getOrgAuditEventsByCorrId("rogfk.no", mongoAuditEvent.corrId)

        then:
        events.size() == 1
    }

    def "Get Org Audit Events By Source"() {
        when:
        List<MongoAuditEvent> events = auditEventMongoRepository.getOrgAuditEventsBySource("rogfk.no", "Organisasjon")

        then:
        events.size() == 1
    }

}
