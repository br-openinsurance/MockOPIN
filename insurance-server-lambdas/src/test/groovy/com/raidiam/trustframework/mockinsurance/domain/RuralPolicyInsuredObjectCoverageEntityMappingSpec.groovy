package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralInsuredObjectCoverage

class RuralPolicyInsuredObjectCoverageEntityMappingSpec extends Specification {

    def "a rural coverage stored with the spec wire value DIAS_UTEIS maps without a 500 (regression)"() {
        given: "a rural coverage whose grace period counting method is the wire value DIAS_UTEIS"
            def coverage = aRuralPolicyInsuredObjectCoverage()
            coverage.setGracePeriodCountingMethod("DIAS_UTEIS")

        when: "the policy info is built for that coverage"
            def result = coverage.mapDto()

        then: "the request succeeds and the wire value is preserved"
            noExceptionThrown()
            result.getGracePeriodCountingMethod() == InsuranceRuralInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS
            result.getGracePeriodCountingMethod().toString() == "DIAS_UTEIS"
    }

    private static RuralPolicyInsuredObjectCoverageEntity aRuralPolicyInsuredObjectCoverage() {
        def coverage = new RuralPolicyInsuredObjectCoverageEntity()
        coverage.setBranch("0111")
        coverage.setCode(InsuranceRuralInsuredObjectCoverage.CodeEnum.GRANIZO.toString())
        coverage.setDescription("string")
        coverage.setInternalCode("string")
        coverage.setSusepProcessNumber("string")
        coverage.setLmiAmount("1")
        coverage.setLmiUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        coverage.setLmiUnitTypeOthers("Horas")
        coverage.setLmiUnitCode("R\$")
        coverage.setLmiUnitDescription(AmountDetailsUnit.DescriptionEnum.BRL.toString())
        coverage.setIsLMISublimit(true)
        coverage.setTermStartDate(LocalDate.of(2022, 12, 31))
        coverage.setTermEndDate(LocalDate.of(2022, 12, 31))
        coverage.setIsMainCoverage(true)
        coverage.setFeature(InsuranceRuralInsuredObjectCoverage.FeatureEnum.MASSIFICADOS.toString())
        coverage.setType(InsuranceRuralInsuredObjectCoverage.TypeEnum.PARAMETRICO.toString())
        coverage.setGracePeriod(0)
        coverage.setGracePeriodicity(InsuranceRuralInsuredObjectCoverage.GracePeriodicityEnum.DIA.toString())
        coverage.setGracePeriodCountingMethod("DIAS_UTEIS")
        coverage.setGracePeriodStartDate(LocalDate.of(2022, 12, 31))
        coverage.setGracePeriodEndDate(LocalDate.of(2022, 12, 31))
        coverage.setPremiumPeriodicity(InsuranceRuralInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL.toString())
        coverage.setPremiumPeriodicityOthers("string")
        coverage
    }
}
