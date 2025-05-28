package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.EndorsementEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.CreateConsentDataEndorsementInformation;
import com.raidiam.trustframework.mockinsurance.models.generated.CreateEndorsementData;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;

@Singleton
@Transactional
public class EndorsementService extends BaseInsuranceService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EndorsementService.class);

    @Inject 
    ConsentService consentService;

    public EndorsementEntity createEndorsement(EndorsementEntity endorsement){
        validate(endorsement);

        LOG.info("Creating endorsement");

        return endorsementRepository.save(endorsement);
    }

    private void validate(EndorsementEntity endorsement) {
        LOG.info("Validating endorsement");

        // Consent ID
        if (StringUtils.isBlank(endorsement.getConsentId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent id was not informed");
        }

        ConsentEntity consent = consentService.getConsentEntity(endorsement.getConsentId(), endorsement.getClientId());

        // Client ID
        if (!endorsement.getClientId().equals(consent.getClientId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: Requested a consent created with a different oauth client");
        }

        // Consent Status
        if (!consent.getStatus().equals(EnumConsentStatus.AUTHORISED.toString())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: consent is not authorised");
        }
        
        CreateConsentDataEndorsementInformation consentEndorsementInfo = consent.getEndorsementInformation();

        CreateEndorsementData endorsementData = endorsement.getData();
        
        // Consent Endorsement Information
        if (consentEndorsementInfo == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent does not have endorsement information");
        }

        // Request Date
        if (endorsementData.getRequestDate() == null){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: request date was not informed");
        }

        if (endorsementData.getRequestDate().isAfter(LocalDate.now())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: request date is invalid");
        }

        // Endorsement Type
        if(endorsementData.getEndorsementType() == null){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: endorsement type was not informed");
        }
        
        if (!endorsementData.getEndorsementType().toString().equals(consentEndorsementInfo.getEndorsementType().toString())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: endorsement type does not match");
        }
        
        // Insured Object ID
        if (endorsementData.getInsuredObjectId() == null || endorsementData.getInsuredObjectId().isEmpty()){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: insured object id was not informed");
        }
        
        // Make sure we're comparing the same thing
        Collections.sort(endorsementData.getInsuredObjectId());
        Collections.sort(consentEndorsementInfo.getInsuredObjectId());
        
        if (!endorsementData.getInsuredObjectId().equals(consentEndorsementInfo.getInsuredObjectId())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: insured object id does not match");
        }
        
        // Policy ID
        if (endorsementData.getPolicyId() == null){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: policy id was not informed");
        }
        
        if (!endorsementData.getPolicyId().equals(consentEndorsementInfo.getPolicyId())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: policy id does not match");
        }
        
        // Proposal ID
        if (endorsementData.getProposalId() == null){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: proposal id was not informed");
        }
        
        if (!endorsementData.getProposalId().equals(consentEndorsementInfo.getProposalId())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: proposal id does not match");
        }
        
        // Request Description
        if(endorsementData.getRequestDescription() == null){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: request description was not informed");
        }
        
        if (!endorsementData.getRequestDescription().equals(consentEndorsementInfo.getRequestDescription())){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: request description does not match");
        }
    }

}
