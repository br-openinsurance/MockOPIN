package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.ClaimNotificationData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import java.util.Date;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = false)
@Audited
@MappedSuperclass
public abstract class ClaimNotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "claim_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID claimId;

    @Column(name = "consent_id", nullable = false)
    private String consentId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @EqualsAndHashCode.Exclude
    @Column(name = "expiration_date_time")
    private Date expirationDateTime;

    public abstract ClaimNotificationData getClaimData();
}
