package com.raidiam.trustframework.mockinsurance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.Base64;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "idempotency_records")
public class IdempotencyEntity extends BaseEntity {

    @Id
    @Column(name = "idempotency_id", unique = true, nullable = false, updatable = false)
    private String idempotencyId;

    @NotNull
    @Column(name = "request")
    private String request;

    @NotNull
    @Column(name = "response")
    private String response;

    public String getPlainResponse() {
        return new String(Base64.getDecoder().decode(this.response));
    }
}
