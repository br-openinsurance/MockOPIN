package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "capitalization_title_withdrawals")
public class WithdrawalCapitalizationTitleEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "withdrawal_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID withdrawalId;

    @Column(name = "consent_id", nullable = false)
    private String consentId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "data")
    @Type(JsonType.class)
    private WithdrawalData data;

    public static WithdrawalCapitalizationTitleEntity fromRequest(RequestCapitalizationTitleWithdrawal req, String consentId, String clientId) {
        var entity = new WithdrawalCapitalizationTitleEntity();
        entity.setConsentId(consentId);
        entity.setClientId(clientId);
        var data = new WithdrawalData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static WithdrawalCapitalizationTitleEntity fromRequestV2(RequestCapitalizationTitleWithdrawalV2 req, String consentId, String clientId) {
        var entity = new WithdrawalCapitalizationTitleEntity();
        entity.setConsentId(consentId);
        entity.setClientId(clientId);
        var data = new WithdrawalData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseCapitalizationTitleWithdrawal toResponse(String redirectLink) {
        var responseData = new ResponseCapitalizationTitleWithdrawalData();
        responseData.setProtocolNumber("string");
        responseData.setProtocolDateTime(InsuranceLambdaUtils.getOffsetDateTimeInBrasil());
        responseData.setModality(ResponseCapitalizationTitleWithdrawalData.ModalityEnum.valueOf(this.data.getV1().getModality().toString()));
        responseData.setSusepProcessNumber(this.data.getV1().getSusepProcessNumber());
        responseData.setProductInformation(this.data.getV1().getProductInformation());
        responseData.setWithdrawalInformation(this.data.getV1().getWithdrawalInformation());
        responseData.setRedirectLink(redirectLink);
        responseData.setWithdrawalCustomData(this.data.getV1().getWithdrawalCustomData());

        var resp = new ResponseCapitalizationTitleWithdrawal();
        resp.setData(responseData);
        return resp;
    }

    public ResponseCapitalizationTitleWithdrawalV2 toResponseV2(String redirectLink) {
        var responseData = new ResponseCapitalizationTitleWithdrawalData();
        responseData.setProtocolNumber("string");
        responseData.setProtocolDateTime(InsuranceLambdaUtils.getOffsetDateTimeInBrasil());
        responseData.setModality(ResponseCapitalizationTitleWithdrawalData.ModalityEnum.valueOf(this.data.getV1().getModality().toString()));
        responseData.setSusepProcessNumber(this.data.getV1().getSusepProcessNumber());
        responseData.setProductInformation(this.data.getV1().getProductInformation());
        responseData.setWithdrawalInformation(this.data.getV1().getWithdrawalInformation());
        responseData.setRedirectLink(redirectLink);
        responseData.setWithdrawalCustomData(this.data.getV1().getWithdrawalCustomData());

        var resp = new ResponseCapitalizationTitleWithdrawalV2();
        resp.setData(responseData);
        return resp;
    }

    @Data
    public static class WithdrawalData implements Serializable {
        @JsonProperty("v1")
        private RequestCapitalizationTitleWithdrawalData v1;

        @JsonIgnore
        public String getCapitalizationTitleName() {
            return v1.getProductInformation().getCapitalizationTitleName();
        }

        @JsonIgnore
        public String getPlanId() {
            return v1.getProductInformation().getPlanId();
        }

        @JsonIgnore
        public String getTitleId() {
            return v1.getProductInformation().getTitleId();
        }

        @JsonIgnore
        public String getSeriesId() {
            return v1.getProductInformation().getSeriesId();
        }

        @JsonIgnore
        public LocalDate getTermEndDate() {
            return v1.getProductInformation().getTermEndDate();
        }

        @JsonIgnore
        public String getWithdrawalReason() {
            var info = v1.getWithdrawalInformation();
            if (info != null && info.getWithdrawalReason() != null) {
                return info.getWithdrawalReason().toString();
            }
            return null;
        }
    }
}
