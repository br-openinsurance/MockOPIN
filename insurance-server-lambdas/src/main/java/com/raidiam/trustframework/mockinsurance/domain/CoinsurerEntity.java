package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.Coinsurer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "coinsurers")
public class CoinsurerEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "coinsurer_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID coinsurerId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "ceded_percentage")
    private String cededPercentage;

    public Coinsurer mapDTO() {
        return new Coinsurer()
                .identification(this.getIdentification())
                .cededPercentage(this.getCededPercentage());
    }
}
