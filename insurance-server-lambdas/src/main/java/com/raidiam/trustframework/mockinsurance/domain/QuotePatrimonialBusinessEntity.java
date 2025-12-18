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
@Table(name = "patrimonial_business_quotes")
public class QuotePatrimonialBusinessEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

    public static QuotePatrimonialBusinessEntity fromRequest(QuoteRequestPatrimonialBusiness req, String clientId) {
        var entity = new QuotePatrimonialBusinessEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuoteData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static QuotePatrimonialBusinessEntity fromRequestV2(QuoteRequestPatrimonialBusinessV2 req, String clientId) {
        var entity = new QuotePatrimonialBusinessEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuoteData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseQuotePatrimonialBusiness toResponse() {
        var quoteData = new ResponseQuotePatrimonialBusinessData();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteResultPremium();
            premium.setPaymentsQuantity(1);
            premium.setTotalPremiumAmount(new AmountDetails()
                    .amount("100.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setTotalNetAmount(new AmountDetails()
                    .amount("50.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setIOF(new AmountDetails()
                    .amount("20.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setCoverages(List.of());
            premium.setPayments(List.of(new QuoteResultPayment()
                    .amount(new AmountDetails()
                            .amount("100.00")
                            .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                            .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)))
                    .paymentType(QuoteResultPayment.PaymentTypeEnum.PIX)));

            var quote = new QuotePatrimonialItem();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuotePatrimonialBusiness();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuotePatrimonialBusiness();
        resp.setData(quoteData);
        return resp;
    }

    public ResponseQuotePatrimonialBusinessV2 toResponseV2() {
        var quoteData = new ResponseQuotePatrimonialBusinessV2Data();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomerV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteResultPremium();
            premium.setPaymentsQuantity(1);
            premium.setTotalPremiumAmount(new AmountDetails()
                    .amount("100.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setTotalNetAmount(new AmountDetails()
                    .amount("50.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setIOF(new AmountDetails()
                    .amount("20.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setCoverages(List.of());
            premium.setPayments(List.of(new QuoteResultPayment()
                    .amount(new AmountDetails()
                            .amount("100.00")
                            .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                            .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)))
                    .paymentType(QuoteResultPayment.PaymentTypeEnum.PIX)));

            var quote = new QuotePatrimonialItem();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuotePatrimonialBusinessV2();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuotePatrimonialBusinessV2();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        LocalDate termStartDate;
        LocalDate termEndDate;
        String policyId;
        if (this.getData().getV1() != null) {
            termStartDate = this.getData().getV1().getQuoteData().getTermStartDate();
            termEndDate = this.getData().getV1().getQuoteData().getTermEndDate();
            policyId = Optional.ofNullable(this.getData().getV1())
                    .map(QuotePatrimonialBusinessData::getQuoteData)
                    .map(QuoteDataPatrimonialBusiness::getPolicyId).orElse(null);
        } else {
            termStartDate = this.getData().getV2().getQuoteData().getTermStartDate();
            termEndDate = this.getData().getV2().getQuoteData().getTermEndDate();
            policyId = Optional.ofNullable(this.getData().getV2())
                    .map(QuotePatrimonialBusinessDataV2::getQuoteData)
                    .map(QuoteDataPatrimonialBusinessV2::getPolicyId).orElse(null);
        }

        boolean endDateBeforeStartDate = Optional.ofNullable(termStartDate)
                .flatMap(start -> Optional.ofNullable(termEndDate)
                        .map(end -> end.isBefore(start)))
                .orElse(false);
        return (policyId == null) || endDateBeforeStartDate;
    }

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private QuotePatrimonialBusinessData v1;
        @JsonProperty("v2")
        private QuotePatrimonialBusinessDataV2 v2;

        @JsonIgnore
        public AmountDetails getMaxLMG() {
            if (this.getV1() != null) {
                return this.getV1().getQuoteData().getMaxLMG();
            }
            return this.getV2().getQuoteData().getMaxLMG();
        }
    }
}
