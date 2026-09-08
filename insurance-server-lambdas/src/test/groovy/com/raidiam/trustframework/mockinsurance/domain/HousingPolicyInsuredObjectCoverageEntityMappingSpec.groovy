package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingInsuredObjectCoverage

class HousingPolicyInsuredObjectCoverageEntityMappingSpec extends Specification {

    def "a housing coverage stored with the spec wire value DIAS_UTEIS maps without a 500 (regression)"() {
        given: "a housing coverage whose grace period counting method is the wire value DIAS_UTEIS"
            def coverage = aHousingPolicyInsuredObjectCoverage()
            coverage.setGracePeriodCountingMethod("DIAS_UTEIS")

        when: "the coverage dto is built"
            def result = coverage.mapDto()

        then: "the request succeeds and the wire value is preserved"
            noExceptionThrown()
            result.getGracePeriodCountingMethod() == InsuranceHousingInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS
            result.getGracePeriodCountingMethod().toString() == "DIAS_UTEIS"
    }

    private static HousingPolicyInsuredObjectCoverageEntity aHousingPolicyInsuredObjectCoverage() {
        def coverage = new HousingPolicyInsuredObjectCoverageEntity()
        coverage.setBranch("0977")
        coverage.setCode(InsuranceHousingInsuredObjectCoverage.CodeEnum.DANOS_ELETRICOS.toString())
        coverage.setDescription("string")
        coverage.setInternalCode("string")
        coverage.setSusepProcessNumber("string")
        coverage.setLMIAmount("100")
        coverage.setLMIUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        coverage.setIsLMISublimit(true)
        coverage.setTermStartDate(LocalDate.of(2022, 12, 31))
        coverage.setTermEndDate(LocalDate.of(2023, 12, 31))
        coverage.setIsMainCoverage(true)
        coverage.setFeature(InsuranceHousingInsuredObjectCoverage.FeatureEnum.MASSIFICADOS.toString())
        coverage.setType(InsuranceHousingInsuredObjectCoverage.TypeEnum.PARAMETRICO.toString())
        coverage.setGracePeriod(0)
        coverage.setGracePeriodicity(InsuranceHousingInsuredObjectCoverage.GracePeriodicityEnum.DIA.toString())
        coverage.setGracePeriodCountingMethod("DIAS_UTEIS")
        coverage.setPremiumPeriodicity(InsuranceHousingInsuredObjectCoverage.PremiumPeriodicityEnum.ANUAL.toString())
        coverage
    }
}
