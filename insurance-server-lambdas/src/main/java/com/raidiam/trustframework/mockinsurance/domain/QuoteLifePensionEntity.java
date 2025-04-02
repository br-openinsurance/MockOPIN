package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_quotes")
public class QuoteLifePensionEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private RequestContractLifePensionData data;

    public static QuoteLifePensionEntity fromRequest(RequestContractLifePension req, String clientId) {
        var entity = new QuoteLifePensionEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        entity.setData(req.getData());
        return entity;
    }

    public QuoteStatusLifePension toResponse() {
        var quoteData = new QuoteStatusLifePensionData();
        quoteData.setStatus(QuoteStatusLifePensionData.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getQuoteCustomer().getQualificationData());
            
            var generalPlan = new GeneralPlanDataQuoteInfoLifePension();
            generalPlan.setEapcName("Previdência ABCD S.A.");
            
            var quote = new QuoteInfoLifePensionQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setGeneralPlan(generalPlan);
            quote.setGeneralDataEstablishedFie(List.of());
            

            var quoteInfo = new QuoteInfoLifePension();
            quoteInfo.setQuoteCustomData(this.getData().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getQuoteData());
            quoteInfo.setQuotes(List.of(quote));
            quoteInfo.setQuoteCustomer(customer);

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason(QuoteStatusLifePensionData.RejectionReasonEnum.fromValue("SEM_OFERTA_PRODUTO"));
        }

        var resp = new QuoteStatusLifePension();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        var initialContribution = this.getData().getQuoteData().getProducts().get(0).getInitialContribution();
        var initialContributionAmount = initialContribution.getAmount();
        var initialContributionUnitType = initialContribution.getUnitType();
        return initialContributionAmount.equals("1000.00") && initialContributionUnitType.toString().equals("PORCENTAGEM");
    }

    public ResponsePatchLifePension toResponsePatchLifePension(String redirectLink) {
        var patchData = new ResponsePatchLifePensionData();
        patchData.setStatus(ResponsePatchLifePensionData.StatusEnum.fromValue(this.getStatus()));

        if (QuoteStatusEnum.ACKN.toString().equals(this.getStatus())) {
            patchData.setInsurerQuoteId(this.getQuoteId().toString());
            patchData.setProtocolNumber("12345678");
            patchData.setProtocolDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            patchData.setRedirectLink(redirectLink);
        }

        var resp = new ResponsePatchLifePension();
        resp.setData(patchData);
        return resp;
    }
}
