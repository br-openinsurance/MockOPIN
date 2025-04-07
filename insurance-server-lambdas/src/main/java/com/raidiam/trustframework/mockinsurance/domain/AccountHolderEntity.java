package com.raidiam.trustframework.mockinsurance.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "account_holders")
public class AccountHolderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Generated()
    @Column(name = "account_holder_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID accountHolderId;

    @Column(name = "document_identification")
    private String documentIdentification;

    @Column(name = "document_rel")
    private String documentRel;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    //this field is the user id queried by the OP, mapping to sub
    @Column(name = "user_id")
    private String userId;
}
