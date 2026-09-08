package com.raidiam.trustframework.mockinsurance.utils;

import com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import static com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress.TypeEnum.RUA;
import static lombok.AccessLevel.PRIVATE;
import static org.slf4j.LoggerFactory.getLogger;

@NoArgsConstructor(access = PRIVATE)
public class AddressParser {

    private static final Logger LOG = getLogger(AddressParser.class);

    public static TypeEnum parseNationalAddressType(String address) {

        LOG.debug("Parsing National Address type from: {}", address);

        String token = address.contains(" ") ?
                address.split(" ", 2)[0] : address;
        try {
            return TypeEnum.valueOf(token.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RUA;
        }
    }

    public static String[] parseNationalAddressNameAndNumber(String address) {

        LOG.debug("Parsing National Address name and number from: {}", address);

        String rest = address.contains(" ") ?
                address.split(" ", 2)[1] : address;

        String[] parts = rest.split(",", 2);

        String number = parts.length > 1 ? parts[1].trim() : "";

        return new String[]{parts[0].trim(), number.isEmpty() ? "15" : number};
    }

}
