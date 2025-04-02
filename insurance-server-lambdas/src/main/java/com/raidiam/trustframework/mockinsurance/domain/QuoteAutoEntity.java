package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteAutoData;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteAutoResultPremium;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteCustomer;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestAuto;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteResultPayment;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusAuto;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusAutoQuotes;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteAuto;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteAutoData;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteAutoData.StatusEnum;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "auto_quotes")
public class QuoteAutoEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteAutoData data;

    @Override
    public boolean shouldReject() {
        return this.getData().getQuoteData().getTermStartDate().isAfter(this.getData().getQuoteData().getTermEndDate());
    }

    public static QuoteAutoEntity fromRequest(QuoteRequestAuto req, String clientId) {
        QuoteAutoEntity entity = new QuoteAutoEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        entity.setData(req.getData());

        return entity;
    }

    public ResponseQuoteAuto toResponse() {
        var quoteData = new ResponseQuoteAutoData();
        quoteData.setStatus(StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteAutoResultPremium();
            premium.setPaymentsQuantity("1");
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

            var quote = new QuoteStatusAutoQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumbers(List.of());
            quote.setAssistances(List.of());
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuoteStatusAuto();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuoteAuto();
        resp.setData(quoteData);
        return resp;
    }

}
