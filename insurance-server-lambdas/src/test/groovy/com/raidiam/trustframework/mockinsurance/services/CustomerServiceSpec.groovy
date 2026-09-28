package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.BusinessIdentificationEntity
import com.raidiam.trustframework.mockinsurance.domain.BusinessQualificationEntity
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity
import com.raidiam.trustframework.mockinsurance.domain.PersonalIdentificationEntity
import com.raidiam.trustframework.mockinsurance.domain.PersonalQualificationEntity
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

    private static final String ALPHANUMERIC_CNPJ = "SAZZEED9000169"
    private static final String V1_NUMERIC_CNPJ = "50685362006768"
    private static final String COMPANY_CNPJ = "01773247000537"

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

            testBusinessIdentification = businessIdentificationRepository.save(TestEntityDataFactory.aBusinessIdentification(accountHolder.getAccountHolderId(), ALPHANUMERIC_CNPJ))
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

    def "given a business identification with an alphanumeric CNPJ, when retrieving the V2 resource, then it responds with the alphanumeric CNPJ"() {
        given: "the stored entity carries an alphanumeric CNPJ"
        assert testBusinessIdentification.getCnpjNumber() == ALPHANUMERIC_CNPJ

        when:
        def data = customerService.getBusinessIdentificationsV2(consent.getConsentId()).getData().first()

        then: "the alphanumeric CNPJ is passed through untouched"
        data.getDocument().getBusinesscnpjNumber() == ALPHANUMERIC_CNPJ
        data.getCompanyInfo().getCnpjNumber() == COMPANY_CNPJ
    }

    def "given a business identification with an alphanumeric CNPJ, when retrieving the V1 resource, then it responds with a numeric CNPJ"() {
        given: "the stored entity carries an alphanumeric CNPJ"
        assert testBusinessIdentification.getCnpjNumber() == ALPHANUMERIC_CNPJ

        when:
        def data = customerService.getBusinessIdentifications(consent.getConsentId()).getData().first()

        then: "v1 returns its own numeric CNPJ, never the alphanumeric column value"
        data.getDocument().getBusinesscnpjNumber() == V1_NUMERIC_CNPJ
        data.getDocument().getBusinesscnpjNumber() != testBusinessIdentification.getCnpjNumber()
        data.getCompanyInfo().getCnpjNumber() == COMPANY_CNPJ
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

    def "we can get personal identifications V2 with all required NATIONAL address fields" () {
        when:
        def response = customerService.getPersonalIdentificationsV2(consent.getConsentId())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().get(0).getPersonalId() == testPersonalIdentification.getPersonalIdentificationsId().toString()

        when:
        def address = response.getData().get(0).getContact().getPostalAddresses().get(0).getAddress().getAddress()

        then: "all OPIN v2 required NATIONAL address fields populated on the AllOfAddressAddress subclass (the fields Jackson actually serializes)"
        address.getType() != null
        address.getAllOfAddressAddressName() == "Naburo Ykesaki"
        address.getAllOfAddressAddressNumber() == "1270"
        address.getAllOfAddressAddressTownName() == "Sao Paulo"
        address.getAllOfAddressAddressCountrySubDivision() == "SP"
        address.getAllOfAddressAddressPostCode() == "10000000"
    }

    def "given a personal identification, when retrieving the V1 and V2 resources, then both respond with the numeric companyInfo CNPJ"() {
        when:
        def v1Data = customerService.getPersonalIdentifications(consent.getConsentId()).getData().first()

        then:
        v1Data.getCompanyInfo().getCnpjNumber() == COMPANY_CNPJ

        when:
        def v2Data = customerService.getPersonalIdentificationsV2(consent.getConsentId()).getData().first()

        then:
        v2Data.getCompanyInfo().getCnpjNumber() == COMPANY_CNPJ
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
