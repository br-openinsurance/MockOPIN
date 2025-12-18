package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class CustomerServiceSpec extends CleanupSpecification {

    @Inject
    CustomerService customerService

    @Shared
    AccountHolderEntity accountHolder

    @Shared
    ConsentEntity consent

    @Shared
    BusinessIdentificationEntity testBusinessIdentification
    @Shared
    BusinessQualificationEntity testBusinessQualification

    @Shared
    PersonalIdentificationEntity testPersonalIdentification
    @Shared
    PersonalQualificationEntity testPersonalQualification

    def setup () {
        if(runSetup) {

            accountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            consent = TestEntityDataFactory.aConsent(accountHolder.getAccountHolderId(),
                    EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ,
                    EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ,
                    EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ,
                    EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ,
                    EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ,
                    EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ)
            consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            consent = consentRepository.save(consent)

            testBusinessIdentification = businessIdentificationRepository.save(TestEntityDataFactory.aBusinessIdentification(accountHolder.getAccountHolderId(), "00000000"))
            testBusinessQualification = businessQualificationRepository.save(TestEntityDataFactory.aBusinessQualification(accountHolder.getAccountHolderId()))

            testPersonalIdentification = personalIdentificationRepository.save(TestEntityDataFactory.aPersonalIdentification(accountHolder.getAccountHolderId()))
            testPersonalQualification = personalQualificationRepository.save(TestEntityDataFactory.aPersonalQualification(accountHolder.getAccountHolderId()))

            runSetup = false
        }
    }

    def "we can get business identifications" () {
        when:
        def response = customerService.getBusinessIdentifications(consent.getConsentId())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()

        when:
        def responseData = response.getData().first()

        then:
        responseData.getBusinessId() == testBusinessIdentification.getBusinessIdentificationId().toString()
    }

    def "we can get business identifications V2" () {
        when:
        def response = customerService.getBusinessIdentificationsV2(consent.getConsentId())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()

        when:
        def responseData = response.getData().first()

        then:
        responseData.getBusinessId() == testBusinessIdentification.getBusinessIdentificationId().toString()
    }

    def "we can get business complimentary information" () {
        when:
        def response = customerService.getBusinessComplimentaryInfo(consent.getConsentId())

        then:
        response.getData() != null
    }

    def "we can get business complimentary information" () {
        when:
        def response = customerService.getBusinessComplimentaryInfoV2(consent.getConsentId())

        then:
        response.getData() != null
    }

    def "we can get business qualifications" () {
        when:
        def response = customerService.getBusinessQualifications(consent.getConsentId())

        then:
        response.getData() != null
    }

    def "we can get personal identifications" () {
        when:
        def response = customerService.getPersonalIdentifications(consent.getConsentId())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().get(0).getPersonalId() == testPersonalIdentification.getPersonalIdentificationsId().toString()
    }

    def "we can get personal identifications" () {
        when:
        def response = customerService.getPersonalIdentificationsV2(consent.getConsentId())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().get(0).getPersonalId() == testPersonalIdentification.getPersonalIdentificationsId().toString()
    }

    def "we can get personal financial-relations" () {
        when:
        def response = customerService.getPersonalComplimentaryInfo(consent.getConsentId())

        then:
        response.getData() != null
    }

    def "we can get personal qualifications" () {
        when:
        def response = customerService.getPersonalQualifications(consent.getConsentId())

        then:
        response.getData() != null
    }

    def "we cannot get response without authorised status"() {
        setup:
        def errorMessage = "Bad request, consent not Authorised!"
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.name())
        consentRepository.update(consent)

        when:
        customerService.getPersonalIdentifications( consent.getConsentId())

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.UNAUTHORIZED
        e.getMessage() == errorMessage

        when:
        customerService.getPersonalQualifications(consent.getConsentId())

        then:
        HttpStatusException e1 = thrown()
        e1.status == HttpStatus.UNAUTHORIZED
        e1.getMessage() == errorMessage

        when:
        customerService.getPersonalComplimentaryInfo(consent.getConsentId())

        then:
        HttpStatusException e2 = thrown()
        e2.status == HttpStatus.UNAUTHORIZED
        e2.getMessage() == errorMessage

        when:
        customerService.getBusinessQualifications(consent.getConsentId())

        then:
        HttpStatusException e3 = thrown()
        e3.status == HttpStatus.UNAUTHORIZED
        e3.getMessage() == errorMessage

        when:
        customerService.getBusinessIdentifications(consent.getConsentId())

        then:
        HttpStatusException e4 = thrown()
        e4.status == HttpStatus.UNAUTHORIZED
        e4.getMessage() == errorMessage

        when:
        customerService.getBusinessComplimentaryInfo(consent.getConsentId())

        then:
        HttpStatusException e5 = thrown()
        e5.status == HttpStatus.UNAUTHORIZED
        e5.getMessage() == errorMessage
    }

    def "we cannot get response V2 without authorised status"() {
        setup:
        def errorMessage = "Bad request, consent not Authorised!"
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.name())
        consentRepository.update(consent)

        when:
        customerService.getPersonalIdentificationsV2( consent.getConsentId())

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.UNAUTHORIZED
        e.getMessage() == errorMessage

        when:
        customerService.getBusinessIdentificationsV2(consent.getConsentId())

        then:
        HttpStatusException e4 = thrown()
        e4.status == HttpStatus.UNAUTHORIZED
        e4.getMessage() == errorMessage

        when:
        customerService.getBusinessComplimentaryInfoV2(consent.getConsentId())

        then:
        HttpStatusException e5 = thrown()
        e5.status == HttpStatus.UNAUTHORIZED
        e5.getMessage() == errorMessage
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
