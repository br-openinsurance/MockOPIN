package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Transactional
public class CustomerService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private List<PersonalIdentificationEntity> getPersonalIdentificationEntities(String consentId){
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ, EnumConsentV3Permission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ);
    
        return personalIdentificationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
    }

    public ResponsePersonalCustomersIdentification getPersonalIdentifications(String consentId) {
        var personalIdentifications = getPersonalIdentificationEntities(consentId);
        var data = personalIdentifications.stream().map(PersonalIdentificationEntity::mapDto).toList();
        return new ResponsePersonalCustomersIdentification().data(data);
    }

    public ResponsePersonalCustomersIdentificationV2 getPersonalIdentificationsV2(String consentId) {
        var personalIdentifications = getPersonalIdentificationEntities(consentId);
        var data = personalIdentifications.stream().map(PersonalIdentificationEntity::mapDtoV2).toList();
        return new ResponsePersonalCustomersIdentificationV2().data(data);
    }

    public ResponsePersonalCustomersQualification getPersonalQualifications(String consentId) {
        LOG.info("Getting Personal Customers Qualification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ, EnumConsentV3Permission.CUSTOMERS_PERSONAL_QUALIFICATION_READ);

        var personalQualifications = personalQualificationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
        var data = personalQualifications.stream().map(PersonalQualificationEntity::mapDto).toList();
        return new ResponsePersonalCustomersQualification().data(data);
    }

    public ResponsePersonalCustomersComplimentaryInformation getPersonalComplimentaryInfo(String consentId) {
        LOG.info("Getting Personal Customers Complimentary Information response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ, EnumConsentV3Permission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ);

        var personalInfo = personalComplimentaryInformationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
        var data = personalInfo.stream().map(PersonalComplimentaryInformationEntity::mapDto).toList();
        return new ResponsePersonalCustomersComplimentaryInformation().data(data);
    }

    private List<BusinessIdentificationEntity> getBusinessIdentificationEntities(String consentId) {
        LOG.info("Getting Business Customers Identification response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ, EnumConsentV3Permission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ);
    
        return businessIdentificationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
    }

    public ResponseBusinessCustomersIdentification getBusinessIdentifications(String consentId) {
        var businessIdentifications = getBusinessIdentificationEntities(consentId);
        var data = businessIdentifications.stream().map(BusinessIdentificationEntity::mapDto).toList();
        return new ResponseBusinessCustomersIdentification().data(data);
    }

    public ResponseBusinessCustomersIdentificationV2 getBusinessIdentificationsV2(String consentId) {
        var businessIdentifications = getBusinessIdentificationEntities(consentId);
        var data = businessIdentifications.stream().map(BusinessIdentificationEntity::mapDtoV2).toList();
        return new ResponseBusinessCustomersIdentificationV2().data(data);
    }

    public ResponseBusinessCustomersQualification getBusinessQualifications(String consentId) {
        LOG.info("Getting Business Customers Qualification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ, EnumConsentV3Permission.CUSTOMERS_BUSINESS_QUALIFICATION_READ);

        var businessIdentifications = businessQualificationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
        var data = businessIdentifications.stream().map(BusinessQualificationEntity::mapDto).toList();
        return new ResponseBusinessCustomersQualification().data(data);
    }

    private List<BusinessComplimentaryInformationEntity> getBusinessComplimentaryInfoEntities(String consentId) {
        LOG.info("Getting Business Customers Complimentary Information response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ, EnumConsentV3Permission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ);
    
        return businessComplimentaryInformationRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId());
    }

    public ResponseBusinessCustomersComplimentaryInformation getBusinessComplimentaryInfo(String consentId) {
        var businessInfo = getBusinessComplimentaryInfoEntities(consentId);
        var data = businessInfo.stream().map(BusinessComplimentaryInformationEntity::mapDto).toList();
        return new ResponseBusinessCustomersComplimentaryInformation().data(data);
    }

    public ResponseBusinessCustomersComplimentaryInformationV2 getBusinessComplimentaryInfoV2(String consentId) {
        var businessInfo = getBusinessComplimentaryInfoEntities(consentId);
        var data = businessInfo.stream().map(BusinessComplimentaryInformationEntity::mapDtoV2).toList();
        return new ResponseBusinessCustomersComplimentaryInformationV2().data(data);
    }
}
