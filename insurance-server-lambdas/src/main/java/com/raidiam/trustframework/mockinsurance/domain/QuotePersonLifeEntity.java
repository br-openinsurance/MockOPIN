package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

import java.io.Serializable;
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
    private QuoteData data;

    public static QuotePersonLifeEntity fromRequest(QuoteRequestPersonLife req, String clientId) {
        var entity = new QuotePersonLifeEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePersonLifeEntity.QuoteData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static QuotePersonLifeEntity fromRequestV2(QuoteRequestPersonLifeV2 req, String clientId) {
        var entity = new QuotePersonLifeEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePersonLifeEntity.QuoteData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseQuoteStatusPersonLife toResponse() {
        var quoteData = new ResponseQuoteStatusPersonLifeData();
        quoteData.setStatus(ResponseQuoteStatusPersonLifeData.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var quoteInfo = new QuoteStatusPersonLife();
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());

            var customer = new PersonalCustomerInfo();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());

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

    public ResponseQuotePersonLifeV2 toResponseV2() {
        var quoteData = new ResponseQuotePersonLifeV2Data();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var quoteInfo = new QuoteStatusPersonLifeV2();
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());

            var customer = new PersonalCustomerInfoV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());

            quoteInfo.setQuoteCustomer(customer);

            var quote = new QuoteStatusPersonLifeV2Quotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setSusepProcessNumbers(List.of("susep_number"));

            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("SEM_OFERTA_PRODUTO");
        }

        var resp = new ResponseQuotePersonLifeV2();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        LocalDate termStartDate = this.getData().getTermStartDate();
        LocalDate termEndDate = this.getData().getTermEndDate();

        return Optional.ofNullable(termStartDate)
                .flatMap(start -> Optional.ofNullable(termEndDate)
                        .map(end -> end.isBefore(start)))
                .orElse(false);
    }

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private QuoteRequestPersonLifeData v1;
        @JsonProperty("v2")
        private QuotePersonLifeDataV2 v2;

        @JsonIgnore
        public LocalDate getTermStartDate() {
            if (this.getV1() != null) {
                return this.getV1().getQuoteData().getTermStartDate();
            }
            return this.getV2().getQuoteData().getTermStartDate();
        }

        @JsonIgnore
        public LocalDate getTermEndDate() {
            if (this.getV1() != null) {
                return this.getV1().getQuoteData().getTermEndDate();
            }
            return this.getV2().getQuoteData().getTermEndDate();
        }
    }
}
