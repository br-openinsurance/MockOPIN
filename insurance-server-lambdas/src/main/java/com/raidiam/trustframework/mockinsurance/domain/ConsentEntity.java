package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "consents")
public class ConsentEntity extends BaseEntity {

    public static final String CONSENT_ID_FORMAT = "urn:raidiaminsurance:%s";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Column(name = "consent_id", nullable = false, updatable = false, insertable = true)
    private String consentId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @Column(name = "expiration_date_time")
    private Date expirationDateTime;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "creation_date_time")
    private Date creationDateTime;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "status_update_date_time")
    private Date statusUpdateDateTime;

    @NotNull
    @Column(name = "status")
    private String status;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejection_code")
    private String rejectionCode;

    @Column(name = "business_document_identification")
    private String businessDocumentIdentification;

    @Column(name = "business_document_rel")
    private String businessDocumentRel;

    @Column(name = "endorsement_information")
    @Type(JsonType.class)
    private CreateConsentDataEndorsementInformation endorsementInformation;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "permissions", columnDefinition = "TEXT[]")
    private List<String> permissions;

    @Column(name = "claim_notification_information")
    @Type(JsonType.class)
    private ClaimNotificationInformation claimNotificationInformation;

    public static ConsentEntity fromRequest(CreateConsent req, UUID accountHolderId, String clientId) {
        ConsentEntity entity = new ConsentEntity();
        UUID uuid = UUID.randomUUID();
        String consentId = String.format(CONSENT_ID_FORMAT, uuid);
        entity.setConsentId(consentId);
        entity.setClientId(clientId);

        entity.setAccountHolderId(accountHolderId);
        if (req.getData().getBusinessEntity() != null) {
            entity.setBusinessDocumentIdentification(req.getData().getBusinessEntity().getDocument().getIdentification());
            entity.setBusinessDocumentRel(req.getData().getBusinessEntity().getDocument().getRel());
        }

        entity.setCreationDateTime(Date.from(Instant.now()));
        entity.setStatusUpdateDateTime(Date.from(Instant.now()));
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString());
        Optional.ofNullable(req.getData().getBusinessEntity())
                .map(BusinessEntity::getDocument)
                .ifPresent(d -> entity.setBusinessDocumentIdentification(d.getIdentification()));
        entity.setPermissions(req.getData().getPermissions()
                .stream()
                .map(permission -> Optional.ofNullable(permission).map(EnumConsentPermission::toString).orElse(null))
                .toList());
        entity.setEndorsementInformation(req.getData().getEndorsementInformation());

        entity.setClaimNotificationInformation(req.getData().getClaimNotificationInformation());

        return entity;
    }

    public ConsentEntity updateFromRequest(UpdateConsent req) {
        if (req.getData().getStatus() != null) {
            this.setStatus(req.getData().getStatus().toString());
            if (EnumConsentStatus.REJECTED.equals(req.getData().getStatus())) {
                this.setRejectionCode(EnumReasonCode.CUSTOMER_MANUALLY_REJECTED.name());
                this.setRejectedBy(EnumRejectedBy.USER.name());
            }
        }
        return this;
    }

    public ResponseConsent toFullResponse() {
        var resp = this.toResponse();
        if (this.accountHolderId != null) {
            resp.getData().setSub(accountHolder.getUserId());
            resp.getData().setLoggedUser(new LoggedUser()
                    .document(new LoggedUserDocument()
                            .identification(accountHolder.getDocumentIdentification())
                            .rel(accountHolder.getDocumentRel())));
        }
        if (businessDocumentIdentification != null) {
            resp.getData().setBusinessEntity(new BusinessEntity()
                    .document(new BusinessEntityDocument()
                            .identification(businessDocumentIdentification)
                            .rel(businessDocumentRel))
            );
        }

        return resp;
    }

    public ResponseConsent toResponse() {
        ResponseConsentData consentData = new ResponseConsentData()
                .creationDateTime(InsuranceLambdaUtils.dateToOffsetDate(creationDateTime))
                .statusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(statusUpdateDateTime))
                .status(EnumConsentStatus.fromValue(status))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .consentId(consentId)
                .permissions(this.getPermissions()
                .stream()
                .map(EnumConsentPermission::valueOf)
                .toList());

        if (EnumConsentStatus.REJECTED.toString().equals(this.status)) {
            consentData.rejection(new ResponseConsentDataRejection()
                    .rejectedBy(EnumRejectedBy.fromValue(this.rejectedBy))
                    .reason(new RejectedReason()
                            .code(EnumReasonCode.fromValue(this.rejectionCode))
                            .additionalInformation("the consent was rejected")));
        }

        return new ResponseConsent().data(consentData);
    }
}
