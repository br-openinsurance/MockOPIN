package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "pension_withdrawal_leads")
public class WithdrawalPensionLeadEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "withdrawal_lead_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID withdrawalLeadId;

    @Column(name = "consent_id", nullable = false)
    private String consentId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "data")
    @Type(JsonType.class)
    private WithdrawalData data;

    public static WithdrawalPensionLeadEntity fromRequest(RequestPensionWithdrawal req, String consentId, String clientId) {
        var entity = new WithdrawalPensionLeadEntity();
        entity.setConsentId(consentId);
        entity.setClientId(clientId);
        var data = new WithdrawalData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static WithdrawalPensionLeadEntity fromRequestV2(RequestPensionWithdrawalV2 req, String consentId, String clientId) {
        var entity = new WithdrawalPensionLeadEntity();
        entity.setConsentId(consentId);
        entity.setClientId(clientId);
        var data = new WithdrawalData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseWithdrawalLead toResponse() {
        var resp = new ResponseWithdrawalLead();
        resp.setData(this.data.getV1());
        return resp;
    }

    public ResponseWithdrawalLeadV2 toResponseV2(RequestPensionWithdrawalV2Data reqData) {
        var resp = new ResponseWithdrawalLeadV2();
        resp.setData(reqData);
        return resp;
    }

    @Data
    public static class WithdrawalData implements Serializable {
        @JsonProperty("v1")
        private RequestPensionWithdrawalData v1;
        @JsonProperty("v2")
        private RequestPensionWithdrawalV2Data v2;

        @JsonIgnore
        public String getCertificateId() {
            if (v1 != null) return v1.getGeneralInfo().getCertificateId();
            return v2.getGeneralInfo().getCertificateId();
        }

        @JsonIgnore
        public String getProductName() {
            if (v1 != null) return v1.getGeneralInfo().getProductName();
            return v2.getGeneralInfo().getProductName();
        }

        @JsonIgnore
        public String getWithdrawalType() {
            if (v1 != null && v1.getWithdrawalInfo() != null && v1.getWithdrawalInfo().getWithdrawalType() != null) {
                return v1.getWithdrawalInfo().getWithdrawalType().toString();
            }
            if (v2 != null && v2.getWithdrawalInfo() != null && v2.getWithdrawalInfo().getWithdrawalType() != null) {
                return v2.getWithdrawalInfo().getWithdrawalType().toString();
            }
            return null;
        }

        @JsonIgnore
        public String getWithdrawalReason() {
            if (v1 != null && v1.getWithdrawalInfo() != null && v1.getWithdrawalInfo().getWithdrawalReason() != null) {
                return v1.getWithdrawalInfo().getWithdrawalReason().toString();
            }
            if (v2 != null && v2.getWithdrawalInfo() != null && v2.getWithdrawalInfo().getWithdrawalReason() != null) {
                return v2.getWithdrawalInfo().getWithdrawalReason().toString();
            }
            return null;
        }
    }
}
