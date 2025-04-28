package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.RafflePaymentInformationCapitalizationTitleRaffle;
import com.raidiam.trustframework.mockinsurance.models.generated.RequestCapitalizationTitleRaffle;
import com.raidiam.trustframework.mockinsurance.models.generated.RequestCapitalizationTitleRaffleData;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseCapitalizationTitleRaffle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseCapitalizationTitleRaffleData;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.RafflePaymentInformationCapitalizationTitleRaffle.RaffleResultEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseCapitalizationTitleRaffleData.ModalityEnum;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_raffles")
public class CapitalizationTitleRaffleEntity extends BaseEntity {

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Id
    @GeneratedValue
    @Column(name = "raffle_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID raffleId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "data")
    @Type(JsonType.class)
    private RequestCapitalizationTitleRaffleData data;

    public static CapitalizationTitleRaffleEntity fromRequest(RequestCapitalizationTitleRaffle req, String clientId) {
        CapitalizationTitleRaffleEntity entity = new CapitalizationTitleRaffleEntity();
        entity.setClientId(clientId);
        entity.setData(req.getData());

        return entity;
    }

    public ResponseCapitalizationTitleRaffle toResponse() {
        var paymentInfo = new RafflePaymentInformationCapitalizationTitleRaffle();
        paymentInfo.setRaffleResult(RaffleResultEnum.CONTEMPLADO);
        paymentInfo.setRaffleResultOthers("string");
        paymentInfo.setPrizeValue(new AmountDetails()
            .amount("33025.67")
            .unitType(UnitTypeEnum.PORCENTAGEM)
            .unitTypeOthers("Horas")
            .unit(new AmountDetailsUnit()
                .code("R$")
                .description(DescriptionEnum.BRL)
            )
        );

        var raffleData = new ResponseCapitalizationTitleRaffleData();
        raffleData.setModality(ModalityEnum.valueOf(this.getData().getModality().toString()));
        raffleData.setProtocolDateTime(InsuranceLambdaUtils.getOffsetDateTimeInBrasil());
        raffleData.setProtocolNumber("string");
        raffleData.setRaffleCustomData(this.getData().getRaffleCustomData());
        raffleData.setRafflePaymentInformation(paymentInfo);
        raffleData.setRedirectLink(REDIRECT_LINK);
        raffleData.setSusepProcessNumber(this.getData().getSusepProcessNumber());

        var resp = new ResponseCapitalizationTitleRaffle();
        resp.setData(raffleData);
        return resp;
    }
}