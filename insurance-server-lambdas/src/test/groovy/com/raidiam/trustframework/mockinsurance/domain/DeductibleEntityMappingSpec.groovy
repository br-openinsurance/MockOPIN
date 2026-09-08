package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit
import com.raidiam.trustframework.mockinsurance.models.generated.Deductible
import com.raidiam.trustframework.mockinsurance.models.generated.DeductibleV2

class DeductibleEntityMappingSpec extends Specification {

    def "a deductible stored with the spec wire value DIAS_UTEIS maps to v1 without a 500 (regression)"() {
        given: "a deductible whose period counting method is the wire value DIAS_UTEIS"
            def deductible = aDeductible()
            deductible.setPeriodCountingMethod("DIAS_UTEIS")

        when: "the deductible dto is built"
            def result = deductible.mapDTO()

        then: "the request succeeds and the wire value is preserved"
            noExceptionThrown()
            result.getPeriodCountingMethod() == Deductible.PeriodCountingMethodEnum.UTEIS
            result.getPeriodCountingMethod().toString() == "DIAS_UTEIS"
    }

    def "a deductible stored with the spec wire value DIAS_UTEIS maps to v2 without a 500 (regression)"() {
        given: "a deductible whose period counting method is the wire value DIAS_UTEIS"
            def deductible = aDeductible()
            deductible.setPeriodCountingMethod("DIAS_UTEIS")

        when: "the deductible v2 dto is built"
            def result = deductible.mapDTOV2()

        then: "the request succeeds and the wire value is preserved"
            noExceptionThrown()
            result.getPeriodCountingMethod() == DeductibleV2.PeriodCountingMethodEnum.UTEIS
            result.getPeriodCountingMethod().toString() == "DIAS_UTEIS"
    }

    private static DeductibleEntity aDeductible() {
        def deductible = new DeductibleEntity()
        deductible.setType(Deductible.TypeEnum.DEDUTIVEL.toString())
        deductible.setTypeAdditionalInfo("string")
        deductible.setAmount("20422775042005.26")
        deductible.setUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        deductible.setUnitTypeOthers("Horas")
        deductible.setUnitCode("R\$")
        deductible.setUnitDescription(AmountDetailsUnit.DescriptionEnum.BRL.toString())
        deductible.setCurrency(AmountDetails.CurrencyEnum.BRL.toString())
        deductible.setPeriod(10)
        deductible.setPeriodicity(Deductible.PeriodicityEnum.DIA.toString())
        deductible.setPeriodCountingMethod("DIAS_UTEIS")
        deductible.setPeriodStartDate(LocalDate.of(2022, 5, 16))
        deductible.setPeriodEndDate(LocalDate.of(2022, 5, 17))
        deductible.setDescription("Franquia de exemplo")
        deductible
    }
}
