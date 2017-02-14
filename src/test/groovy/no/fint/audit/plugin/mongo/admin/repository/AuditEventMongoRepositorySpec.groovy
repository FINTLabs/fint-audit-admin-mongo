package no.fint.audit.plugin.mongo.admin.repository

import no.fint.audit.plugin.mongo.admin.model.MongoAuditEvent
import no.fint.audit.plugin.mongo.admin.model.MongoAuditEventGroup
import no.fint.audit.plugin.mongo.admin.model.PageableAuditEventGroup
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

    // TODO: Find out why this is failing, when the repository method clearly works
    // java.lang.IllegalArgumentException: Expected DBObject or Map, got: d377ca10-34a3-4529-8ebf-b938e80127d0
    def "Get All Audit Events"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.getAllAuditEvents(null, 1, 10)

        then:
        pageable.data.size() == 5
    }

    def "Get All Audit Events By Source"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.search(null, "Arbeidstaker", 1, 10)

        then:
        pageable.data.size() == 2
    }

    def "Get All Audit Events By CorrId"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.search(null, mongoAuditEvent.corrId, 1, 10)

        then:
        pageable.data.size() == 1
    }

    def "Get Org Audit Events"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.getAllAuditEvents("vaf.no", 1, 10)

        then:
        pageable.data.size() == 2
    }

    def "Get Org Audit Events By CorrId"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.search("rogfk.no", mongoAuditEvent.corrId, 1, 10)

        then:
        pageable.data.size() == 1
    }

    def "Get Org Audit Events By Source"() {
        when:
        PageableAuditEventGroup pageable = auditEventMongoRepository.search("rogfk.no", "Organisasjon", 1, 10)

        then:
        pageable.data.size() == 1
    }
}
