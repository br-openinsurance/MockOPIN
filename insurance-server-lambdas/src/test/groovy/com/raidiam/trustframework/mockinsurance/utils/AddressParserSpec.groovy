package com.raidiam.trustframework.mockinsurance.utils

import spock.lang.Specification

import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.AVENIDA
import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.RUA
import static com.raidiam.trustframework.mockinsurance.utils.AddressParser.parseNationalAddressNameAndNumber
import static com.raidiam.trustframework.mockinsurance.utils.AddressParser.parseNationalAddressType

class AddressParserSpec extends Specification {

    def "parseNationalAddressType resolves the leading token to a national address type (#description)"() {
        given: "a raw address string"
            def address = rawAddress

        when: "the address type is parsed"
            def type = parseNationalAddressType(address)

        then: "the expected national address type is returned"
            type == expectedType

        where:
            description                                  | rawAddress                     | expectedType
            "well-formed address"                        | "Avenida Naburo Ykesaki, 1270" | AVENIDA
            "lower case type token"                       | "avenida Naburo Ykesaki, 1270" | AVENIDA
            "unrecognised type token"                     | "Foo Naburo Ykesaki, 1270"     | RUA
            "malformed address with no space"             | "address"                      | RUA
    }

    def "parseNationalAddressNameAndNumber splits the street name and number out of a raw address (#description)"() {
        given: "a raw address string"
            def address = rawAddress

        when: "the street name and number are parsed"
            def parts = parseNationalAddressNameAndNumber(address)

        then: "the expected name and number are returned"
            parts[0] == expectedName
            parts[1] == expectedNumber

        where:
            description                                  | rawAddress                           | expectedName     | expectedNumber
            "well-formed address"                        | "Avenida Naburo Ykesaki, 1270"       | "Naburo Ykesaki" | "1270"
            "extra whitespace around the number"         | "Avenida Naburo Ykesaki,   1270  "   | "Naburo Ykesaki" | "1270"
            "no comma, so no number present"             | "Avenida Naburo Ykesaki"             | "Naburo Ykesaki" | "15"
            "malformed address with no space or comma"   | "address"                            | "address"        | "15"
    }
}