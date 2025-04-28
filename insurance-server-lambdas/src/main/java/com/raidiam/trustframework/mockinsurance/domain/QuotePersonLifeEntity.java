package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_life_quotes")
public class QuotePersonLifeEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteRequestPersonLifeData data;

    public static QuotePersonLifeEntity fromRequest(QuoteRequestPersonLife req, String clientId) {
        var entity = new QuotePersonLifeEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        entity.setData(req.getData());
        return entity;
    }

    public ResponseQuoteStatusPersonLife toResponse() {
        var quoteData = new ResponseQuoteStatusPersonLifeData();
        quoteData.setStatus(ResponseQuoteStatusPersonLifeData.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var quoteInfo = new QuoteStatusPersonLife();
            quoteInfo.setQuoteCustomData(this.getData().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getQuoteData());

            var customer = new PersonalCustomerInfo();
            customer.setIdentification(this.getData().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getQuoteCustomer().getQualificationData());

            quoteInfo.setQuoteCustomer(customer);

            var quote = new QuoteStatusPersonLifeQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setSusepProcessNumbers(List.of("susep_number"));

            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("SEM_OFERTA_PRODUTO");
        }

        var resp = new ResponseQuoteStatusPersonLife();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        LocalDate termStartDate = this.getData().getQuoteData().getTermStartDate();
        LocalDate termEndDate = this.getData().getQuoteData().getTermEndDate();

        return Optional.ofNullable(termStartDate)
                .flatMap(start -> Optional.ofNullable(termEndDate)
                        .map(end -> end.isBefore(start)))
                .orElse(false);
    }
}
