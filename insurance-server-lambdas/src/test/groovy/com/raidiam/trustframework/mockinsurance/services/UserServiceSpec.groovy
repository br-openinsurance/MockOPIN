package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.AutoPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanEntity
import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractEntity
import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractEntity
import com.raidiam.trustframework.mockinsurance.domain.AcceptanceAndBranchesAbroadPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PatrimonialPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractEntity
import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.ResponsibilityPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class UserServiceSpec extends CleanupSpecification {

    @Inject
    UserService userService

    @Shared
    LifePensionContractEntity testLifePensionContract

    @Shared
    FinancialRiskPolicyEntity testFinancialRiskPolicy

    @Shared
    PensionPlanContractEntity testPensionPlanContract;

    @Shared
    HousingPolicyEntity testHousingPolicy

    @Shared
    PersonPolicyEntity testPersonPolicy

    @Shared
    ResponsibilityPolicyEntity testResponsibilityPolicy;

    @Shared
    CapitalizationTitlePlanEntity testCapitalizationTitlePlan

    @Shared
    FinancialAssistanceContractEntity testFinancialAssistanceContract

    @Shared
    AcceptanceAndBranchesAbroadPolicyEntity testAcceptanceAndBranchesAbroadPolicy

    @Shared
    PatrimonialPolicyEntity testPatrimonialPolicy

    @Shared
    AutoPolicyEntity testAutoPolicy

    @Shared
    RuralPolicyEntity testRuralPolicy;

    @Shared
    TransportPolicyEntity testTransportPolicy

    @Shared
    AccountHolderEntity testAccountHolder

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testLifePensionContract = lifePensionContractRepository.save(TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId()))
            testPensionPlanContract = pensionPlanContractRepository.save(TestEntityDataFactory.aPensionPlanContract("pension-plan-1", testAccountHolder.getAccountHolderId()))
            testFinancialRiskPolicy = financialRiskPolicyRepository.save(TestEntityDataFactory.aFinancialRiskPolicy(testAccountHolder.getAccountHolderId()))
            testHousingPolicy = housingPolicyRepository.save(TestEntityDataFactory.aHousingPolicy(testAccountHolder.getAccountHolderId()))
            testResponsibilityPolicy = responsibilityPolicyRepository.save(TestEntityDataFactory.aResponsibilityPolicy(testAccountHolder.getAccountHolderId()))
            testPersonPolicy = personPolicyRepository.save(TestEntityDataFactory.aPersonPolicy(testAccountHolder.getAccountHolderId()))
            testCapitalizationTitlePlan = capitalizationTitlePlanRepository.save(TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId()))
            testFinancialAssistanceContract = financialAssistanceContractRepository.save(TestEntityDataFactory.aFinancialAssistanceContract(testAccountHolder.getAccountHolderId()))
            testAcceptanceAndBranchesAbroadPolicy = acceptanceAndBranchesAbroadPolicyRepository.save(TestEntityDataFactory.anAcceptanceAndBranchesAbroadPolicy(testAccountHolder.getAccountHolderId()))
            testPatrimonialPolicy = patrimonialPolicyRepository.save(TestEntityDataFactory.aPatrimonialPolicy(testAccountHolder.getAccountHolderId(), "0111"))
            testRuralPolicy = ruralPolicyRepository.save(TestEntityDataFactory.aRuralPolicy(testAccountHolder.getAccountHolderId()))
            testAutoPolicy = autoPolicyRepository.save(TestEntityDataFactory.anAutoPolicy(testAccountHolder.getAccountHolderId()))
            testTransportPolicy = transportPolicyRepository.save(TestEntityDataFactory.aTransportPolicy(testAccountHolder.getAccountHolderId()))
            runSetup = false
        }
    }

    def "we can get life pension contracts"() {
        when:
        def resp = userService.getLifePensionContracts(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get pension plan contracts"() {
        when:
        def resp = userService.getPensionPlanContracts(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get financial risk policies"() {
        when:
        def resp = userService.getFinancialRiskPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get housing policies"() {
        when:
        def resp = userService.getHousingPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get responsibility policies"() {
        when:
        def resp = userService.getResponsibilityPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get person policies"() {
        when:
        def resp = userService.getPersonPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get capitalization title plans"() {
        when:
        def resp = userService.getCapitalizationTitlePlans(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get financial assistance contracts"() {
        when:
        def resp = userService.getFinancialAssistanceContracts(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get acceptance and branches abroad policies"() {
        when:
        def resp = userService.getAcceptanceAndBranchesAbroadPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get patrimonial policies"() {
        when:
        def resp = userService.getPatrimonialPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get rural policies"() {
        when:
        def resp = userService.getRuralPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get auto policies"() {
        when:
        def resp = userService.getAutoPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "we can get transport policies"() {
        when:
        def resp = userService.getTransportPolicies(testAccountHolder.getUserId())

        then:
        resp.getData().size() == 1
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
