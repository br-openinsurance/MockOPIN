package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "consents")
public class ConsentEntity extends BaseEntity {

    public static final String CONSENT_ID_FORMAT = "urn:raidiaminsurance:%s";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Column(name = "consent_id", nullable = false, updatable = false, insertable = true)
    private String consentId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @Column(name = "expiration_date_time")
    private Date expirationDateTime;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "creation_date_time")
    private Date creationDateTime;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "status_update_date_time")
    private Date statusUpdateDateTime;

    @NotNull
    @Column(name = "status")
    private String status;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejection_code")
    private String rejectionCode;

    @Column(name = "business_document_identification")
    private String businessDocumentIdentification;

    @Column(name = "business_document_rel")
    private String businessDocumentRel;

    @Column(name = "endorsement_information")
    @Type(JsonType.class)
    private CreateConsentDataEndorsementInformation endorsementInformation;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "permissions", columnDefinition = "TEXT[]")
    private List<String> permissions;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_life_pension_contracts",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id"))
    private Set<LifePensionContractEntity> lifePensionContracts = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_pension_plan_contracts",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id"))
    private Set<PensionPlanContractEntity> pensionPlanContracts = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_financial_risk_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id"))
    private Set<FinancialRiskPolicyEntity> financialRiskPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_housing_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id"))
    private Set<HousingPolicyEntity> housingPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_responsibility_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "responsibility_policy_id", referencedColumnName = "responsibility_policy_id"))
    private Set<ResponsibilityPolicyEntity> responsibilityPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_person_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "person_policy_id", referencedColumnName = "person_policy_id"))
    private Set<PersonPolicyEntity> personPolicies = new HashSet<>();

    @Column(name = "claim_notification_information")
    @Type(JsonType.class)
    private ClaimNotificationInformation claimNotificationInformation;

    @Column(name = "withdrawal_captalization_information")
    @Type(JsonType.class)
    private CreateConsentDataWithdrawalCaptalizationInformation withdrawalCaptalizationInformation;

    @Column(name = "withdrawal_life_pension_information")
    @Type(JsonType.class)
    private CreateConsentDataWithdrawalLifePensionInformation withdrawalLifePensionInformation;

    @Column(name = "raffle_captalization_title_information")
    @Type(JsonType.class)
    private CreateConsentDataRaffleCaptalizationTitleInformation raffleCaptalizationTitleInformation;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_capitalization_title_plans",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id"))
    private Set<CapitalizationTitlePlanEntity> capitalizationTitlePlans = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_acceptance_and_branches_abroad_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "policy_id"))
    private Set<AcceptanceAndBranchesAbroadPolicyEntity> acceptanceAndBranchesAbroadPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_patrimonial_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "policy_id"))
    private Set<PatrimonialPolicyEntity> patrimonialPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_rural_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id"))
    private Set<RuralPolicyEntity> ruralPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_financial_assistance_contracts",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "financial_assistance_contract_id", referencedColumnName = "financial_assistance_contract_id"))
    private Set<FinancialAssistanceContractEntity> financialAssistanceContracts = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_auto_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "auto_policy_id", referencedColumnName = "auto_policy_id"))
    private Set<AutoPolicyEntity> autoPolicies = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "consent_transport_policies",
            joinColumns = @JoinColumn(name = "consent_id", referencedColumnName = "consent_id"),
            inverseJoinColumns = @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id"))
    private Set<TransportPolicyEntity> transportPolicies = new HashSet<>();

    public static ConsentEntity fromRequest(CreateConsent req, UUID accountHolderId, String clientId) {
        ConsentEntity entity = new ConsentEntity();
        UUID uuid = UUID.randomUUID();
        String consentId = String.format(CONSENT_ID_FORMAT, uuid);
        entity.setConsentId(consentId);
        entity.setClientId(clientId);

        entity.setAccountHolderId(accountHolderId);
        if (req.getData().getBusinessEntity() != null) {
            entity.setBusinessDocumentIdentification(req.getData().getBusinessEntity().getDocument().getIdentification());
            entity.setBusinessDocumentRel(req.getData().getBusinessEntity().getDocument().getRel());
        }

        entity.setCreationDateTime(Date.from(Instant.now()));
        entity.setStatusUpdateDateTime(Date.from(Instant.now()));
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString());
        Optional.ofNullable(req.getData().getBusinessEntity())
                .map(BusinessEntity::getDocument)
                .ifPresent(d -> entity.setBusinessDocumentIdentification(d.getIdentification()));
        entity.setPermissions(req.getData().getPermissions()
                .stream()
                .map(permission -> Optional.ofNullable(permission).map(EnumConsentPermission::toString).orElse(null))
                .toList());
        entity.setEndorsementInformation(req.getData().getEndorsementInformation());

        entity.setClaimNotificationInformation(req.getData().getClaimNotificationInformation());
        entity.setWithdrawalCaptalizationInformation(req.getData().getWithdrawalCaptalizationInformation());
        entity.setWithdrawalLifePensionInformation(req.getData().getWithdrawalLifePensionInformation());
        entity.setRaffleCaptalizationTitleInformation(req.getData().getRaffleCaptalizationTitleInformation());

        return entity;
    }

    public static ConsentEntity fromRequest(CreateConsentV3 req, UUID accountHolderId, String clientId) {
        ConsentEntity entity = new ConsentEntity();
        UUID uuid = UUID.randomUUID();
        String consentId = String.format(CONSENT_ID_FORMAT, uuid);
        entity.setConsentId(consentId);
        entity.setClientId(clientId);

        entity.setAccountHolderId(accountHolderId);
        if (req.getData().getBusinessEntity() != null) {
            entity.setBusinessDocumentIdentification(req.getData().getBusinessEntity().getDocument().getIdentification());
            entity.setBusinessDocumentRel(req.getData().getBusinessEntity().getDocument().getRel());
        }

        entity.setCreationDateTime(Date.from(Instant.now()));
        entity.setStatusUpdateDateTime(Date.from(Instant.now()));
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString());
        Optional.ofNullable(req.getData().getBusinessEntity())
                .map(BusinessEntity::getDocument)
                .ifPresent(d -> entity.setBusinessDocumentIdentification(d.getIdentification()));
        entity.setPermissions(req.getData().getPermissions()
                .stream()
                .map(permission -> Optional.ofNullable(permission).map(p -> switch (p) {
                        case PENSION_PLAN_CLAIM_READ -> "PENSION_PLAN_CLAIM";
                        case LIFE_PENSION_CLAIM_READ -> "LIFE_PENSION_CLAIM";
                        case CAPITALIZATION_TITLE_RAFFLE_CREATE -> "QUOTE_CAPITALIZATION_TITLE_RAFFLE_CREATE";
                        default -> p.toString();
                }).orElse(null))
                .toList());
        entity.setEndorsementInformation(req.getData().getEndorsementInformation());

        entity.setClaimNotificationInformation(req.getData().getClaimNotificationInformation());
        entity.setWithdrawalCaptalizationInformation(req.getData().getWithdrawalCapitalizationInformation());
        entity.setWithdrawalLifePensionInformation(convertWithdrawalLifePensionV3toV2(req.getData().getWithdrawalLifePensionInformation()));
        entity.setRaffleCaptalizationTitleInformation(req.getData().getRaffleCapitalizationTitleInformation());

        return entity;
    }

    private static CreateConsentDataWithdrawalLifePensionInformation convertWithdrawalLifePensionV3toV2(CreateConsentV3DataWithdrawalLifePensionInformation v3) {
        if (v3 == null) return null;
        return new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(v3.getCertificateId())
                .productName(v3.getProductName())
                .withdrawalType(Optional.ofNullable(v3.getWithdrawalType())
                        .map(t -> switch (t) {
                            case TOTAL -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL;
                            case PARCIAL -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._2_PARCIAL;
                        })
                        .orElse(null))
                .withdrawalReason(Optional.ofNullable(v3.getWithdrawalReason())
                        .map(r -> switch (r) {
                            case EMERGENCIAS_DE_SAUDE -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE;
                            case APLICACAO_EM_OUTROS_INVESTIMENTOS -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._2_APLICACAO_EM_OUTROS_INVESTIMENTOS;
                            case INSATISFACAO_COM_A_ENTIDADE -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._3_INSATISFACAO_COM_A_ENTIDADE;
                            case INSATISFACAO_COM_A_RENTABILIDADE_DO_PRODUTO -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._4_INSATISFACAO_COM_A_RENTABILIDADE_DO_PRODUTO;
                            case INSATISFACAO_COM_O_PRODUTO -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._5_INSATISFACAO_COM_O_PRODUTO;
                            case AQUISICAO_DE_BENS -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._6_AQUISICAO_DE_BENS;
                            case LIQUIDEZ_FINANCEIRA -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._7_LIQUIDEZ_FINANCEIRA;
                            case REALIZACAO_DO_OBJETIVO_DO_INVESTIMENTO -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._8_REALIZACAO_DO_OBJETIVO_DO_INVESTIMENTO;
                            case OUTROS -> CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._9_OUTROS;
                        })
                        .orElse(null))
                .withdrawalReasonOthers(v3.getWithdrawalReasonOthers())
                .desiredTotalAmount(v3.getDesiredTotalAmount())
                .pmbacAmount(v3.getPmbacAmount());
    }

    private static CreateConsentV3DataWithdrawalLifePensionInformation convertWithdrawalLifePensionV2toV3(CreateConsentDataWithdrawalLifePensionInformation v2) {
        if (v2 == null) return null;
        return new CreateConsentV3DataWithdrawalLifePensionInformation()
                .certificateId(v2.getCertificateId())
                .productName(v2.getProductName())
                .withdrawalType(Optional.ofNullable(v2.getWithdrawalType())
                        .map(t -> switch (t) {
                            case _1_TOTAL -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalTypeEnum.TOTAL;
                            case _2_PARCIAL -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalTypeEnum.PARCIAL;
                        })
                        .orElse(null))
                .withdrawalReason(Optional.ofNullable(v2.getWithdrawalReason())
                        .map(r -> switch (r) {
                            case _1_EMERGENCIAS_DE_SAUDE -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.EMERGENCIAS_DE_SAUDE;
                            case _2_APLICACAO_EM_OUTROS_INVESTIMENTOS -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.APLICACAO_EM_OUTROS_INVESTIMENTOS;
                            case _3_INSATISFACAO_COM_A_ENTIDADE -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.INSATISFACAO_COM_A_ENTIDADE;
                            case _4_INSATISFACAO_COM_A_RENTABILIDADE_DO_PRODUTO -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.INSATISFACAO_COM_A_RENTABILIDADE_DO_PRODUTO;
                            case _5_INSATISFACAO_COM_O_PRODUTO -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.INSATISFACAO_COM_O_PRODUTO;
                            case _6_AQUISICAO_DE_BENS -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.AQUISICAO_DE_BENS;
                            case _7_LIQUIDEZ_FINANCEIRA -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.LIQUIDEZ_FINANCEIRA;
                            case _8_REALIZACAO_DO_OBJETIVO_DO_INVESTIMENTO -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.REALIZACAO_DO_OBJETIVO_DO_INVESTIMENTO;
                            case _9_OUTROS -> CreateConsentV3DataWithdrawalLifePensionInformation.WithdrawalReasonEnum.OUTROS;
                        })
                        .orElse(null))
                .withdrawalReasonOthers(v2.getWithdrawalReasonOthers())
                .desiredTotalAmount(v2.getDesiredTotalAmount())
                .pmbacAmount(v2.getPmbacAmount());
    }

    public ResponseConsent toFullResponse() {
        var resp = this.toResponse();
        if (this.accountHolderId != null) {
            resp.getData().setSub(accountHolder.getUserId());
            resp.getData().setLoggedUser(new LoggedUser()
                    .document(new LoggedUserDocument()
                            .identification(accountHolder.getDocumentIdentification())
                            .rel(accountHolder.getDocumentRel())));
        }
        if (businessDocumentIdentification != null) {
            resp.getData().setBusinessEntity(new BusinessEntity()
                    .document(new BusinessEntityDocument()
                            .identification(businessDocumentIdentification)
                            .rel(businessDocumentRel))
            );
        }

        resp.getData().linkedCapitalizationTilePlanIds(capitalizationTitlePlans.stream()
                .map(CapitalizationTitlePlanEntity::getCapitalizationTitlePlanId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedFinancialRiskPolicyIds(financialRiskPolicies.stream()
                .map(FinancialRiskPolicyEntity::getFinancialRiskPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedHousingPolicyIds(housingPolicies.stream()
                .map(HousingPolicyEntity::getHousingPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedResponsibilityPolicyIds(responsibilityPolicies.stream()
                .map(ResponsibilityPolicyEntity::getResponsibilityPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPersonPolicyIds(personPolicies.stream()
                .map(PersonPolicyEntity::getPersonPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedLifePensionContractIds(lifePensionContracts.stream()
                .map(LifePensionContractEntity::getLifePensionContractId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPensionPlanContractIds(pensionPlanContracts.stream()
                .map(PensionPlanContractEntity::getPensionPlanContractId)
                .toList());

        resp.getData().linkedAcceptanceAndBranchesAbroadPolicyIds(acceptanceAndBranchesAbroadPolicies.stream()
                .map(AcceptanceAndBranchesAbroadPolicyEntity::getPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPatrimonialPolicyIds(patrimonialPolicies.stream()
                .map(PatrimonialPolicyEntity::getPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedRuralPolicyIds(ruralPolicies.stream()
                .map(RuralPolicyEntity::getRuralPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedFinancialAssistanceContractIds(financialAssistanceContracts.stream()
                .map(FinancialAssistanceContractEntity::getFinancialAssistanceContractId)
                .toList());

        resp.getData().linkedAutoPolicyIds(autoPolicies.stream()
                .map(AutoPolicyEntity::getAutoPolicyId)
                .toList());

        resp.getData().linkedTransportPolicyIds(transportPolicies.stream()
                .map(TransportPolicyEntity::getTransportPolicyId)
                .toList());

        return resp;
    }

    public ResponseConsentV3 toFullResponseV3() {
        var resp = this.toResponseV3();
        if (this.accountHolderId != null) {
            resp.getData().setSub(accountHolder.getUserId());
            resp.getData().setLoggedUser(new LoggedUser()
                    .document(new LoggedUserDocument()
                            .identification(accountHolder.getDocumentIdentification())
                            .rel(accountHolder.getDocumentRel())));
        }
        if (businessDocumentIdentification != null) {
            resp.getData().setBusinessEntity(new BusinessEntity()
                    .document(new BusinessEntityDocument()
                            .identification(businessDocumentIdentification)
                            .rel(businessDocumentRel))
            );
        }

        resp.getData().linkedCapitalizationTilePlanIds(capitalizationTitlePlans.stream()
                .map(CapitalizationTitlePlanEntity::getCapitalizationTitlePlanId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedFinancialRiskPolicyIds(financialRiskPolicies.stream()
                .map(FinancialRiskPolicyEntity::getFinancialRiskPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedHousingPolicyIds(housingPolicies.stream()
                .map(HousingPolicyEntity::getHousingPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedResponsibilityPolicyIds(responsibilityPolicies.stream()
                .map(ResponsibilityPolicyEntity::getResponsibilityPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPersonPolicyIds(personPolicies.stream()
                .map(PersonPolicyEntity::getPersonPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedLifePensionContractIds(lifePensionContracts.stream()
                .map(LifePensionContractEntity::getLifePensionContractId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPensionPlanContractIds(pensionPlanContracts.stream()
                .map(PensionPlanContractEntity::getPensionPlanContractId)
                .toList());

        resp.getData().linkedAcceptanceAndBranchesAbroadPolicyIds(acceptanceAndBranchesAbroadPolicies.stream()
                .map(AcceptanceAndBranchesAbroadPolicyEntity::getPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedPatrimonialPolicyIds(patrimonialPolicies.stream()
                .map(PatrimonialPolicyEntity::getPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedRuralPolicyIds(ruralPolicies.stream()
                .map(RuralPolicyEntity::getRuralPolicyId)
                .map(UUID::toString)
                .toList());

        resp.getData().linkedFinancialAssistanceContractIds(financialAssistanceContracts.stream()
                .map(FinancialAssistanceContractEntity::getFinancialAssistanceContractId)
                .toList());

        resp.getData().linkedAutoPolicyIds(autoPolicies.stream()
                .map(AutoPolicyEntity::getAutoPolicyId)
                .toList());

        resp.getData().linkedTransportPolicyIds(transportPolicies.stream()
                .map(TransportPolicyEntity::getTransportPolicyId)
                .toList());

        return resp;
    }

    public ResponseConsent toResponse() {
        ResponseConsentData consentData = new ResponseConsentData()
                .creationDateTime(InsuranceLambdaUtils.dateToOffsetDate(creationDateTime))
                .statusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(statusUpdateDateTime))
                .status(EnumConsentStatus.fromValue(status))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .consentId(consentId)
                .endorsementInformation(this.endorsementInformation)
                .claimNotificationInformation(this.claimNotificationInformation)
                .withdrawalCaptalizationInformation(this.withdrawalCaptalizationInformation)
                .withdrawalLifePensionInformation(this.withdrawalLifePensionInformation)
                .raffleCaptalizationTitleInformation(this.raffleCaptalizationTitleInformation)
                .permissions(this.getPermissions()
                .stream()
                .map(EnumConsentPermission::valueOf)
                .toList());

        if (EnumConsentStatus.REJECTED.toString().equals(this.status)) {
            consentData.rejection(new ResponseConsentDataRejection()
                    .rejectedBy(EnumRejectedBy.fromValue(this.rejectedBy))
                    .reason(new RejectedReason()
                            .code(EnumReasonCode.fromValue(this.rejectionCode))
                            .additionalInformation("the consent was rejected")));
        }

        return new ResponseConsent().data(consentData);
    }

    public ResponseConsentV3 toResponseV3() {
        ResponseConsentV3Data consentData = new ResponseConsentV3Data()
                .creationDateTime(InsuranceLambdaUtils.dateToOffsetDate(creationDateTime))
                .statusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(statusUpdateDateTime))
                .status(EnumConsentStatus.fromValue(status))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .expirationDateTime(InsuranceLambdaUtils.dateToOffsetDate(expirationDateTime))
                .consentId(consentId)
                .endorsementInformation(this.endorsementInformation)
                .claimNotificationInformation(this.claimNotificationInformation)
                .withdrawalCapitalizationInformation(this.withdrawalCaptalizationInformation)
                .withdrawalLifePensionInformation(convertWithdrawalLifePensionV2toV3(this.withdrawalLifePensionInformation))
                .raffleCapitalizationTitleInformation(this.raffleCaptalizationTitleInformation)
                .permissions(this.getPermissions().stream().map(p -> switch (p) {
                        case "PENSION_PLAN_CLAIM" -> EnumConsentV3Permission.PENSION_PLAN_CLAIM_READ;
                        case "LIFE_PENSION_CLAIM" -> EnumConsentV3Permission.LIFE_PENSION_CLAIM_READ;
                        case "QUOTE_CAPITALIZATION_TITLE_RAFFLE_CREATE" -> EnumConsentV3Permission.CAPITALIZATION_TITLE_RAFFLE_CREATE;
                        default -> EnumConsentV3Permission.valueOf(p);
                })
                .toList());

        if (EnumConsentStatus.REJECTED.toString().equals(this.status)) {
            consentData.rejection(new ResponseConsentV3DataRejection()
                    .rejectedBy(EnumRejectedBy.fromValue(this.rejectedBy))
                    .reason(new RejectedReasonV3()
                            .code(EnumReasonCodeV3.fromValue(this.rejectionCode))
                            .additionalInformation("the consent was rejected")));
        }

        return new ResponseConsentV3().data(consentData);
    }
}
