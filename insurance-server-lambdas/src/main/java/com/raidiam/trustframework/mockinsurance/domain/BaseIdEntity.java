package com.raidiam.trustframework.mockinsurance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Audited
@MappedSuperclass
public abstract class BaseIdEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID referenceId;

    @Column(name = "name")
    private String name;

    @Column(name = "identification")
    private String identification;

    @Column(name = "identification_type")
    private String identificationType;

    @Column(name = "identification_type_others")
    private String identificationTypeOthers;
}
