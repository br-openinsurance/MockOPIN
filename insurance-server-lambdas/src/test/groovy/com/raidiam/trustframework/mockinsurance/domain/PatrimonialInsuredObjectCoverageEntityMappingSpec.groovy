package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialCoverageCode
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialInsuredObjectCoverage

class PatrimonialInsuredObjectCoverageEntityMappingSpec extends Specification {

    def "a patrimonial coverage stored with the spec wire value DIAS_UTEIS maps without a 500 (regression)"() {
        given: "a patrimonial coverage whose grace period counting method is the wire value DIAS_UTEIS"
            def coverage = aPatrimonialInsuredObjectCoverage()
            coverage.setGracePeriodCountingMethod("DIAS_UTEIS")

        when: "the coverage dto is built"
            def result = coverage.mapDto()

        then: "the request succeeds and the wire value is preserved"
            noExceptionThrown()
            result.getGracePeriodCountingMethod() == InsurancePatrimonialInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS
            result.getGracePeriodCountingMethod().toString() == "DIAS_UTEIS"
    }

    private static PatrimonialInsuredObjectCoverageEntity aPatrimonialInsuredObjectCoverage() {
        def coverage = new PatrimonialInsuredObjectCoverageEntity()
        coverage.setBranch("0977")
        coverage.setCode(InsurancePatrimonialCoverageCode.DANOS_ELETRICOS.toString())
        coverage.setSusepProcessNumber("string")
        coverage.setLmiAmount("100")
        coverage.setLmiUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        coverage.setLmiUnitCode("R\$")
        coverage.setLmiUnitDescription("BRL")
        coverage.setIsLMISublimit(true)
        coverage.setTermStartDate(LocalDate.of(2022, 12, 31))
        coverage.setTermEndDate(LocalDate.of(2023, 12, 31))
        coverage.setIsMainCoverage(true)
        coverage.setFeature(InsurancePatrimonialInsuredObjectCoverage.FeatureEnum.MASSIFICADOS.toString())
        coverage.setType(InsurancePatrimonialInsuredObjectCoverage.TypeEnum.PARAMETRICO.toString())
        coverage.setGracePeriod(0)
        coverage.setGracePeriodicity(InsurancePatrimonialInsuredObjectCoverage.GracePeriodicityEnum.DIA.toString())
        coverage.setGracePeriodCountingMethod("DIAS_UTEIS")
        coverage.setGracePeriodStartDate(LocalDate.of(2022, 12, 31))
        coverage.setGracePeriodEndDate(LocalDate.of(2023, 12, 31))
        coverage.setPremiumPeriodicity(InsurancePatrimonialInsuredObjectCoverage.PremiumPeriodicityEnum.ANUAL.toString())
        coverage
    }
}
