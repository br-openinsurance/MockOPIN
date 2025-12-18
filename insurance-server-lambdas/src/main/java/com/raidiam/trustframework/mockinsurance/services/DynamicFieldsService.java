package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Transactional
public class DynamicFieldsService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicFieldsService.class);

    public DynamicFieldList getDamageAndPerson(Pageable pageable) {
        LOG.info("Getting damage and person dynamic fields information");

        var fields = dynamicFieldsRepository.findAll(pageable);
        List<DynamicFieldListData> data = new ArrayList<>();

        for (DynamicFieldsEntity field : fields) {
            data.add(field.mapDamageAndPersonDTO());
        }

        DynamicFieldList list = new DynamicFieldList();
        list.data(data);
        list.setMeta(InsuranceLambdaUtils.getMeta(fields, false));

        return list;
    }

    public DynamicFieldListV2 getDamageAndPersonV2(Pageable pageable) {
        LOG.info("Getting damage and person dynamic fields information");

        var fields = dynamicFieldsRepository.findAll(pageable);
        List<DynamicFieldListV2Data> data = new ArrayList<>();

        for (DynamicFieldsEntity field : fields) {
            data.add(field.mapDamageAndPersonDTOV2());
        }

        DynamicFieldListV2 list = new DynamicFieldListV2();
        list.data(data);
        list.setMeta(InsuranceLambdaUtils.getMeta(fields, false));

        return list;
    }

    public DynamicFieldsCapitalizationList getCapitalizationTitle() {
        LOG.info("Getting capitalization title dynamic fields information");
        var dynamicField = dynamicFieldsRepository.save(new DynamicFieldsEntity());

        return new DynamicFieldsCapitalizationList().data(List.of(dynamicField.mapCapitalizationTitleDTO()));
    }

    public DynamicFieldsCapitalizationListV2 getCapitalizationTitleV2() {
        LOG.info("Getting capitalization title dynamic fields information");
        var dynamicField = dynamicFieldsRepository.save(new DynamicFieldsEntity());

        return new DynamicFieldsCapitalizationListV2().data(List.of(dynamicField.mapCapitalizationTitleDTOV2()));
    }
}
