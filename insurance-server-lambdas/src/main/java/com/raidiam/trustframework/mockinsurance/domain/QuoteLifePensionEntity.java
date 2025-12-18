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
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_quotes")
public class QuoteLifePensionEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

    public static QuoteLifePensionEntity fromRequest(RequestContractLifePension req, String clientId) {
        var entity = new QuoteLifePensionEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuoteLifePensionEntity.QuoteData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static QuoteLifePensionEntity fromRequestV2(RequestContractLifePensionV2 req, String clientId) {
        var entity = new QuoteLifePensionEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));

        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuoteLifePensionEntity.QuoteData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public QuoteStatusLifePension toResponse() {
        var quoteData = new QuoteStatusLifePensionData();
        quoteData.setStatus(QuoteStatusLifePensionData.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());
            
            var generalPlan = new GeneralPlanDataQuoteInfoLifePension();
            generalPlan.setEapcName("Previdência ABCD S.A.");
            generalPlan.setProductName("product");
            generalPlan.setPlanType(GeneralPlanDataQuoteInfoLifePension.PlanTypeEnum.PGBL);
            generalPlan.setSusepProcessNumber("12345");
            generalPlan.setBiometricTable(GeneralPlanDataQuoteInfoLifePension.BiometricTableEnum.AT_2000_FEMALE_SUAVIZADA_15);
            generalPlan.setRentsInterestRate("10.00");
            generalPlan.setMonetaryUpdateIndex(GeneralPlanDataQuoteInfoLifePension.MonetaryUpdateIndexEnum.IPC_FGV);
            generalPlan.setFinancialResultReversalPercentage("2.00");
            generalPlan.setIsIntendedQualifiedProponents(true);
            generalPlan.setPrioritizationOrder("1");
            
            var quote = new QuoteInfoLifePensionQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setGeneralPlan(generalPlan);
            quote.setGeneralDataEstablishedFie(List.of());
            

            var quoteInfo = new QuoteInfoLifePension();
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());
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

    public QuoteStatusLifePensionV2 toResponseV2() {
        var quoteData = new QuoteStatusLifePensionV2Data();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomerV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());

            var generalPlan = new GeneralPlanDataQuoteInfoLifePension();
            generalPlan.setEapcName("Previdência ABCD S.A.");
            generalPlan.setProductName("product");
            generalPlan.setPlanType(GeneralPlanDataQuoteInfoLifePension.PlanTypeEnum.PGBL);
            generalPlan.setSusepProcessNumber("12345");
            generalPlan.setBiometricTable(GeneralPlanDataQuoteInfoLifePension.BiometricTableEnum.AT_2000_FEMALE_SUAVIZADA_15);
            generalPlan.setRentsInterestRate("10.00");
            generalPlan.setMonetaryUpdateIndex(GeneralPlanDataQuoteInfoLifePension.MonetaryUpdateIndexEnum.IPC_FGV);
            generalPlan.setFinancialResultReversalPercentage("2.00");
            generalPlan.setIsIntendedQualifiedProponents(true);
            generalPlan.setPrioritizationOrder("1");

            var quote = new QuoteInfoLifePensionQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setGeneralPlan(generalPlan);
            quote.setGeneralDataEstablishedFie(List.of());


            var quoteInfo = new QuoteInfoLifePensionV2();
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());
            quoteInfo.setQuotes(List.of(quote));
            quoteInfo.setQuoteCustomer(customer);

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason(QuoteStatusLifePensionV2Data.RejectionReasonEnum.fromValue("SEM_OFERTA_PRODUTO"));
        }

        var resp = new QuoteStatusLifePensionV2();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        var initialContribution = this.getData().getInitialContribution();
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

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private RequestContractLifePensionData v1;
        @JsonProperty("v2")
        private QuoteLifePensionDataV2 v2;

        @JsonIgnore
        public AllOfQuoteDataProductLifePensionInnerInitialContribution getInitialContribution() {
            if (this.getV1() != null) {
                return this.getV1().getQuoteData().getProducts().get(0).getInitialContribution();
            }
            return this.getV2().getQuoteData().getProducts().get(0).getInitialContribution();
        }
    }
}
