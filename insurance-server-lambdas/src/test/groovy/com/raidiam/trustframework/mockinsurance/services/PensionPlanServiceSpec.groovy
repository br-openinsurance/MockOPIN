package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class PensionPlanServiceSpec extends CleanupSpecification {

    @Inject
    PensionPlanService pensionPlanService

    @Shared
    PensionPlanContractEntity testPensionPlanContract

    @Shared
    PensionPlanContractClaimEntity testPensionPlanContractClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentPensionPlanContractEntity testConsentPensionPlanContract

    @Shared
    PensionPlanContractWithdrawalEntity testPensionPlanContractWithdrawal

    @Shared
    PensionPlanContractMovementBenefitEntity testPensionPlanContractMovementBenefit

    @Shared
    PensionPlanContractMovementContributionEntity testPensionPlanContractMovementContribution

    @Shared
    PensionPlanContractPortabilityInfoEntity testPensionPlanContractPortability

    @Shared
    PensionPlanContractDocumentEntity testPensionPlanContractDocument

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.PENSION_PLAN_READ,
                    EnumConsentPermission.PENSION_PLAN_MOVEMENTS_READ,
                    EnumConsentPermission.PENSION_PLAN_PORTABILITIES_READ,
                    EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ,
                    EnumConsentPermission.PENSION_PLAN_CLAIM,
                    EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testPensionPlanContract = pensionPlanContractRepository.save(TestEntityDataFactory.aPensionPlanContract("pension-plan-1", testAccountHolder.getAccountHolderId()))
            consentPensionPlanContractRepository.save(new ConsentPensionPlanContractEntity(testConsent, testPensionPlanContract))
            testPensionPlanContractClaim = pensionPlanContractClaimRepository.save(TestEntityDataFactory.aPensionPlanContractClaim(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractWithdrawal = pensionPlanContractWithdrawalRepository.save(TestEntityDataFactory.aPensionPlanContractWithdrawal(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractMovementBenefit = pensionPlanContractMovementBenefitRepository.save(TestEntityDataFactory.aPensionPlanContractMovementBenefit(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractMovementContribution = pensionPlanContractMovementContributionRepository.save(TestEntityDataFactory.aPensionPlanContractMovementContribution(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractPortability = pensionPlanContractPortabilityRepository.save(TestEntityDataFactory.aPensionPlanContractPortability(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractDocument = pensionPlanContractDocumentRepository.save(TestEntityDataFactory.aPensionPlanContractDocument(testPensionPlanContract.getPensionPlanContractId()))
            pensionPlanContractDocumentInsuredRepository.save(TestEntityDataFactory.aPensionPlanContractDocumentInsured(testPensionPlanContractDocument.getDocumentId()))
            pensionPlanContractDocumentCoverageRepository.save(TestEntityDataFactory.aPensionPlanContractDocumentCoverage(testPensionPlanContractDocument.getDocumentId()))
            runSetup = false
        }
    }

    def "we can get contracts" () {
        when:
        def response = pensionPlanService.getContracts(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get contracts V2" () {
        when:
        def response = pensionPlanService.getContractsV2(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a contract info" () {
        when:
        def response = pensionPlanService.getContractInfo(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getContractingType().toString() == "INDIVIDUAL"
        def document = response.getData().getDocuments().first()
        document.getCertificateId() == "67"
        document.getEffectiveDateStart().toString() == "2021-05-21"
        document.getEffectiveDateEnd().toString() == "2023-05-21"
        document.getProposalId() == "987"
        def insured = document.getInsureds().first()
        insured.getDocumentType().toString() == "CPF"
        insured.getDocumentNumber() == "12345678910"
        insured.getName() == "JOAO DA SILVA"
        insured.getBirthDate().toString() == "2021-05-01"
        insured.getGender().toString() == "FEMININO"
        insured.getPostCode() == "10000000"
        insured.getTownName() == "Sao Paulo"
        insured.getCountrySubDivision().toString() == "SP"
        insured.getCountryCode().toString() == "BRA"
        insured.getAddress() == "Av Naburo Ykesaki, 1270"
        def coverage = document.getPlans().getCoverages().first()
        coverage.getCoverageCode() == "1999"
        coverage.getSusepProcessNumber() == "12345"
        coverage.getStructureModality().toString() == "BENEFICIO_DEFINIDO"
        coverage.getBenefitAmount().getAmount() == "100.00"
        coverage.getBenefitAmount().getUnitType().toString() == "PORCENTAGEM"
        coverage.getPeriodicity().toString() == "MENSAL"
        coverage.getCoverageName() == "coverage"
        coverage.isLockedPlan() == false
        coverage.getTermStartDate().toString() == "2021-05-21"
        coverage.getTermEndDate().toString() == "2023-05-21"
        coverage.getFinancialRegime().toString() == "CAPITALIZACAO"
        coverage.getPricingMethod().toString() == "POR_IDADE"
        coverage.getUpdateIndex().toString() == "IGPM-FGV"
        coverage.getUpdateIndexLagging() == 1
        coverage.getContributionAmount().getAmount() == "100.00"
        coverage.getBenefitPaymentAmount().getAmount() == "100.00"
        coverage.getBenefitPaymentMethod().toString() == "UNICO"
        coverage.getChargedAmount().getAmount() == "100.00"
    }

    def "we can get a contract info V2" () {
        when:
        def response = pensionPlanService.getContractInfoV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getContractingType().toString() == "INDIVIDUAL"
        def document = response.getData().getDocuments().first()
        document.getCertificateId() == "67"
        def insured = document.getInsureds().first()
        insured.getDocumentNumber() == "12345678910"
        insured.getName() == "JOAO DA SILVA"
        insured.getAddress().getFlagPostCode().toString() == "NACIONAL"
        def coverage = document.getPlans().getCoverages().first()
        coverage.getCoverageCode() == "1999"
        coverage.getUpdateIndex().toString() == "IGPM-FGV"
        coverage.getChargedAmount().getAmount() == "100.00"
    }

    def "we can get a contract's claims" () {
        when:
        def response = pensionPlanService.getContractClaims(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        def claim = response.getData().first()
        claim.getEventInfo().getEventStatus().toString() == "ABERTO"
        claim.getEventInfo().getEventAlertDate().toString() == "2021-05-01"
        claim.getEventInfo().getEventRegisterDate().toString() == "2021-05-01"
        claim.getIncomeInfo().getBeneficiaryDocument() == "12345678910"
        claim.getIncomeInfo().getBeneficiaryDocumentType().toString() == "CPF"
        claim.getIncomeInfo().getBeneficiaryName() == "NOME BENEFICIARIO"
        claim.getIncomeInfo().getBeneficiaryCategory().toString() == "SEGURADO"
        claim.getIncomeInfo().getBeneficiaryBirthDate().toString() == "1990-01-01"
        claim.getIncomeInfo().getIncomeType().toString() == "PAGAMENTO_UNICO"
        claim.getIncomeInfo().isReversedIncome() == false
        claim.getIncomeInfo().getIncomeAmount().getAmount() == "10000.00"
        claim.getIncomeInfo().getIncomeAmount().getUnitType().toString() == "MONETARIO"
        claim.getIncomeInfo().getIncomeAmount().getUnit().getCode() == "Br"
        claim.getIncomeInfo().getIncomeAmount().getUnit().getDescription().toString() == "BRL"
        claim.getIncomeInfo().getPaymentTerms() == "PRAZO"
        claim.getIncomeInfo().getBenefitAmount() == 1000
        claim.getIncomeInfo().getGrantedDate().toString() == "2021-05-01"
        claim.getIncomeInfo().getMonetaryUpdateIndex().toString() == "IPC-FGV"
        claim.getIncomeInfo().getLastUpdateDate().toString() == "2021-05-01"
    }

    def "we can get a contract's claims V2" () {
        when:
        def response = pensionPlanService.getContractClaimsV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        def claim = response.getData().first()
        claim.getEventInfo().getEventStatus().toString() == "ABERTO"
        claim.getIncomeInfo().getBeneficiaryDocument() == "12345678910"
        claim.getIncomeInfo().getIncomeAmount().getAmount() == "10000.00"
        claim.getIncomeInfo().getIncomeAmount().getUnit().getDescription().toString() == "BRL"
        claim.getIncomeInfo().getMonetaryUpdateIndex().toString() == "IPC-FGV"
        claim.getIncomeInfo().getLastUpdateDate().toString() == "2021-05-01"
    }

    def "we can get a contract's withdrawals" () {
        when:
        def response = pensionPlanService.getContractWithdrawals(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        def withdrawal = response.getData().first()
        withdrawal.isWithdrawalOccurence() == true
        withdrawal.getType().toString() == "PARCIAL"
        withdrawal.getNature().toString() == "RESGATE_REGULAR"
        withdrawal.getRequestDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        withdrawal.getLiquidationDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        withdrawal.getAmount().getAmount() == "90.85"
        withdrawal.getAmount().getUnitType().toString() == "PORCENTAGEM"
        withdrawal.getPostedChargedAmount().getAmount() == "90.85"
        withdrawal.getPostedChargedAmount().getUnitType().toString() == "PORCENTAGEM"
    }

    def "we can get a contract's withdrawals V2" () {
        when:
        def response = pensionPlanService.getContractWithdrawalsV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        def withdrawal = response.getData().first()
        withdrawal.isWithdrawalOccurence() == true
        def info = withdrawal.getWithdrawalInfo().first()
        info.getType().toString() == "PARCIAL"
        info.getNature().toString() == "RESGATE_REGULAR"
        info.getRequestDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        info.getLiquidationDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        info.getAmount().getAmount() == "90.85"
        info.getAmount().getUnitType().toString() == "PORCENTAGEM"
        info.getPostedChargedAmount().getAmount() == "90.85"
    }

    def "we can get a contract's movements" () {
        when:
        def response = pensionPlanService.getContractMovements(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().getMovementBenefits().first().getBenefitAmount().getAmount() == "95.90"
        response.getData().getMovementBenefits().first().getBenefitAmount().getUnitType().toString() == "PORCENTAGEM"
        response.getData().getMovementBenefits().first().getBenefitPaymentDate().toString() == "2023-10-01"
        response.getData().getMovementContributions().first().getContributionAmount().getAmount() == "95.90"
        response.getData().getMovementContributions().first().getContributionAmount().getUnitType().toString() == "PORCENTAGEM"
        response.getData().getMovementContributions().first().getChargedInAdvanceAmount().getAmount() == "95.90"
        response.getData().getMovementContributions().first().getPeriodicity().toString() == "MENSAL"
        response.getData().getMovementContributions().first().getContributionExpirationDate().toString() == "2022-05-01"
        response.getData().getMovementContributions().first().getContributionPaymentDate().toString() == "2022-05-01"
    }

    def "we can get a contract's portabilities" () {
        when:
        def response = pensionPlanService.getContractPortabilities(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        def info = response.getData().getPortabilityInfo().first()
        info.getDirection().toString() == "ENTRADA"
        info.getType().toString() == "PARCIAL"
        info.getAmount().getAmount() == "90.85"
        info.getAmount().getUnitType().toString() == "PORCENTAGEM"
        info.getRequestDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        info.getLiquidationDate() == java.time.OffsetDateTime.parse("2022-05-20T08:30:00Z")
        info.getChargingValue().getAmount() == "90.85"
        info.getChargingValue().getUnitType().toString() == "PORCENTAGEM"
        info.getSourceEntity() == "12345678901234"
        info.getTargetEntity() == "12345678901234"
        info.getSusepProcess() == "12345"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
