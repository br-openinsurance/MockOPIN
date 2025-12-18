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
@Table(name = "person_travel_quotes")
public class QuotePersonTravelEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

    public static QuotePersonTravelEntity fromRequest(QuoteRequestPersonTravel req, String clientId) {
        var entity = new QuotePersonTravelEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePersonTravelEntity.QuoteData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static QuotePersonTravelEntity fromRequestV2(QuoteRequestPersonTravelV2 req, String clientId) {
        var entity = new QuotePersonTravelEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePersonTravelEntity.QuoteData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseQuoteStatusPersonTravel toResponse() {
        var quoteData = new ResponseQuoteStatusPersonTravelData();
        quoteData.setStatus(ResponseQuoteStatusPersonTravelData.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var quoteInfo = new QuoteStatusPersonTravel();
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());

            var customer = new PersonalCustomerInfo();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());

            quoteInfo.setQuoteCustomer(customer);

            var quote = new QuoteStatusPersonTravelQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setSusepProcessNumbers(List.of("susep_number"));

            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("SEM_OFERTA_PRODUTO");
        }

        var resp = new ResponseQuoteStatusPersonTravel();
        resp.setData(quoteData);
        return resp;
    }

    public ResponseQuotePersonTravelV2 toResponseV2() {
        var quoteData = new ResponseQuotePersonTravelV2Data();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var quoteInfo = new QuotePersonTravelResultV2();
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());

            var customer = new PersonalCustomerInfoV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());

            quoteInfo.setQuoteCustomer(customer);

            var quote = new QuoteStatusPersonTravelQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setSusepProcessNumbers(List.of("susep_number"));

            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("SEM_OFERTA_PRODUTO");
        }

        var resp = new ResponseQuotePersonTravelV2();
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
        private QuoteRequestPersonTravelData v1;
        @JsonProperty("v2")
        private QuotePersonTravelDataV2 v2;

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
