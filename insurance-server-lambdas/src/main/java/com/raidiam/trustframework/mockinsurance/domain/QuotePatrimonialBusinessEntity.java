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
@Table(name = "patrimonial_business_quotes")
public class QuotePatrimonialBusinessEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuotePatrimonialBusinessData data;

    public static QuotePatrimonialBusinessEntity fromRequest(QuoteRequestPatrimonialBusiness req, String clientId) {
        var entity = new QuotePatrimonialBusinessEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        entity.setData(req.getData());
        return entity;
    }

    public ResponseQuotePatrimonialBusiness toResponse() {
        var quoteData = new ResponseQuotePatrimonialBusinessData();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getQuoteCustomer().getComplimentaryInformationData());

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

            var quote = new QuotePatrimonialBusinessQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuotePatrimonialBusiness();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getQuoteCustomData());
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

    @Override
    public boolean shouldReject() {
        LocalDate termStartDate = this.getData().getQuoteData().getTermStartDate();
        LocalDate termEndDate = this.getData().getQuoteData().getTermEndDate();

        boolean endDateBeforeStartDate = Optional.ofNullable(termStartDate)
                .flatMap(start -> Optional.ofNullable(termEndDate)
                        .map(end -> end.isBefore(start)))
                .orElse(false);

        String policyId = Optional.ofNullable(this.getData())
                .map(QuotePatrimonialBusinessData::getQuoteData)
                .map(QuoteDataPatrimonialBusiness::getPolicyId).orElse(null);

        return (policyId == null) || endDateBeforeStartDate;
    }
}
