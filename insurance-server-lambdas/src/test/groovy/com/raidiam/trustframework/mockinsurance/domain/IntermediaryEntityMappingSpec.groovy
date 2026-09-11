package com.raidiam.trustframework.mockinsurance.domain

import spock.lang.Specification

import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.AVENIDA
import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.RUA

class IntermediaryEntityMappingSpec extends Specification {

    def "an intermediary's address is split into street name, number and type on the v2 policy (#description)"() {
        given: "an intermediary with a given address on file"
            def intermediary = anIntermediary(rawAddress)

        when: "the v2 policy info is built for that intermediary"
            def result = intermediary.mapDTOV2()

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

    def "an intermediary's other details are unaffected by how their address is parsed"() {
        given: "a fully populated intermediary"
            def intermediary = anIntermediary("Avenida Naburo Ykesaki, 1270")

        when: "the v2 policy info is built for that intermediary"
            def result = intermediary.mapDTOV2()

        then: "their other details carry over correctly"
            result.getIdentification() == "12345678900"
            result.getName() == "Nome Sobrenome"
            result.getBrokerId() == "17500001"
    }

    def "the v1 policy info shows an intermediary's address exactly as stored"() {
        given: "an intermediary with bad address data on file"
            def intermediary = anIntermediary("address")

        when: "the v1 policy info is built for that intermediary"
            def result = intermediary.mapDTO()

        then: "the address is returned as-is, and the request succeeds"
            noExceptionThrown()
            result.getAddress() == "address"
    }

    private static IntermediaryEntity anIntermediary(String address) {
        def intermediary = new IntermediaryEntity()
        intermediary.setIdentification("12345678900")
        intermediary.setIdentificationType("CPF")
        intermediary.setIdentificationTypeOthers("RNE")
        intermediary.setName("Nome Sobrenome")
        intermediary.setPostCode("17500001")
        intermediary.setBrokerId("17500001")
        intermediary.setCity("Rio de Janeiro")
        intermediary.setState("RJ")
        intermediary.setCountry("BRA")
        intermediary.setAddress(address)
        intermediary.setAddressAdditionalInfo("Fundos")
        intermediary.setFlagPostCode("NACIONAL")
        intermediary.setDistrictName("Botafogo")
        intermediary.setTownCode("3304557")
        intermediary.setType("CORRETOR")
        intermediary
    }
}