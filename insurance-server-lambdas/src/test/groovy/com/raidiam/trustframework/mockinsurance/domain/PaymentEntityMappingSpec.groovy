package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import com.raidiam.trustframework.mockinsurance.models.generated.Payment
import com.raidiam.trustframework.mockinsurance.models.generated.PaymentV2

class PaymentEntityMappingSpec extends Specification {

    def "the v1 premium exposes the teller document detail under the name the spec defines"() {
        given: "a payment whose teller document type is OUTROS, with the detail on file"
            def payment = aPayment()

        when: "the v1 payment dto is built"
            def result = payment.mapDTO()

        then: "the detail is carried over, and the rest of the payment is untouched"
            result.getTellerIdOthers() == "RNE"
            result.getTellerId() == "string"
            result.getTellerIdType() == Payment.TellerIdTypeEnum.CPF
            result.getTellerName() == "string"
    }

    def "the v2 premium exposes the teller document detail under the name the spec defines"() {
        given: "a payment whose teller document type is OUTROS, with the detail on file"
            def payment = aPayment()

        when: "the v2 payment dto is built"
            def result = payment.mapDTOV2()

        then: "the detail is carried over, and the rest of the payment is untouched"
            result.getTellerIdOthers() == "RNE"
            result.getTellerId() == "string"
            result.getTellerIdType() == PaymentV2.TellerIdTypeEnum.CPF
            result.getTellerName() == "string"
    }

    private static PaymentEntity aPayment() {
        def payment = new PaymentEntity()
        payment.setMovementDate(LocalDate.of(2023, 1, 30))
        payment.setMovementType("LIQUIDACAO_DE_PREMIO")
        payment.setMovementOrigin("EMISSAO_DIRETA")
        payment.setMovementPaymentsNumber("str")
        payment.setAmount("55595")
        payment.setUnitType("PORCENTAGEM")
        payment.setUnitTypeOthers("Horas")
        payment.setUnitCode("R\$")
        payment.setUnitDescription("BRL")
        payment.setMaturityDate(LocalDate.of(2023, 1, 30))
        payment.setTellerId("string")
        payment.setTellerIdType("CPF")
        payment.setTellerIdTypeOthers("RNE")
        payment.setTellerName("string")
        payment.setFinancialInstitutionCode("string")
        payment.setPaymentType("BOLETO")
        payment
    }
}
