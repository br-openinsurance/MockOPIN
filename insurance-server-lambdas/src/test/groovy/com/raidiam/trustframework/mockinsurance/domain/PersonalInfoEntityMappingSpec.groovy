package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import java.time.LocalDate

import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.AVENIDA
import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.RUA

class PersonalInfoEntityMappingSpec extends Specification {

    def "an insured's address is split into street name, number and type on the v2 policy (#description)"() {
        given: "an insured with a given address on file"
            def insured = anInsured(rawAddress)

        when: "the v2 policy info is built for that insured"
            def result = insured.mapDTOV2()

        then: "the request succeeds, even with bad address data"
            noExceptionThrown()

        and: "the address is broken down as expected"
            def nationalAddress = result.getAddress().getAddress()
            nationalAddress.getName() == expectedName
            nationalAddress.getNumber() == expectedNumber
            nationalAddress.getType() == expectedType

        where:
            description                        | rawAddress                     | expectedName     | expectedNumber | expectedType
            "a well-formed address"            | "Avenida Naburo Ykesaki, 1270" | "Naburo Ykesaki" | "1270"         | AVENIDA
            "bad address data (500 regression)" | "address"                      | "address"        | "15"           | RUA
    }

    def "an insured's other details are unaffected by how their address is parsed"() {
        given: "a fully populated insured"
            def insured = anInsured("Avenida Naburo Ykesaki, 1270")

        when: "the v2 policy info is built for that insured"
            def result = insured.mapDTOV2()

        then: "their other details carry over correctly"
            result.getIdentification() == "12345678900"
            result.getName() == "Nome Sobrenome"
            result.getEmail() == "string"
            result.getBirthDate() == LocalDate.of(1999, 6, 12)
    }

    def "the v1 policy info shows an insured's address exactly as stored"() {
        given: "an insured with bad address data on file"
            def insured = anInsured("address")

        when: "the v1 policy info is built for that insured"
            def result = insured.mapDTO()

        then: "the address is returned as-is, and the request succeeds"
            noExceptionThrown()
            result.getAddress() == "address"
    }

    private static PersonalInfoEntity anInsured(String address) {
        def insured = new PersonalInfoEntity()
        insured.setIdentification("12345678900")
        insured.setIdentificationType("CPF")
        insured.setIdentificationTypeOthers("RNE")
        insured.setName("Nome Sobrenome")
        insured.setBirthDate(LocalDate.of(1999, 6, 12))
        insured.setPostCode("17500001")
        insured.setEmail("string")
        insured.setCity("Rio de Janeiro")
        insured.setState("RJ")
        insured.setCountry("BRA")
        insured.setAddress(address)
        insured.setAddressAdditionalInfo("Fundos")
        insured.setFlagPostCode("NACIONAL")
        insured.setDistrictName("Botafogo")
        insured.setTownCode("3304557")
        insured
    }
}