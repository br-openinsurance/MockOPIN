package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.domain.DynamicFieldsEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class DynamicFieldsServiceSpec extends CleanupSpecification {

    @Inject
    DynamicFieldsService dynamicFieldsService

    def setup() {
        if (runSetup) {
            def apis = DynamicFieldListData.ApiEnum.values()

            for (def api : apis) {
                def dynamicFieldEntity = new DynamicFieldsEntity()
                dynamicFieldEntity.setApi(api.name())
                dynamicFieldsRepository.save(dynamicFieldEntity)
            }
            runSetup = false
        }
    }

    def "We can fetch dynamic fields for the quote APIs"() {

        when:
        def fieldsList = dynamicFieldsService.getDamageAndPerson(Pageable.from(0, 25))

        then:
        noExceptionThrown()
        fieldsList.getData().size() == DynamicFieldListData.ApiEnum.values().length
        fieldsList.getData().get(0) != null
        fieldsList.getData().get(0).getApi() == DynamicFieldListData.ApiEnum.QUOTE_PATRIMONIAL

        def branch = fieldsList.getData().get(0).getBranch().get(0)
        branch != null
        branch.getCode() == "0111"
        branch.getQuoteRequiredFields().get(0) == "Dependents"
        branch.getFields().get(0) != null
        branch.getFields().get(0).getName() == "Dependents"
        branch.getFields().get(0).getCategory() == Fields.CategoryEnum.RELATIONSHIPINFO
        branch.getFields().get(0).getItems().get(0) != null
        branch.getFields().get(0).getItems().get(0).getFormat() == FieldsArray.FormatEnum.INT32
    }

    def "We can fetch dynamic fields for the capitalization title API"() {

        when:
        def fieldsList = dynamicFieldsService.getCapitalizationTitle()

        then:
        noExceptionThrown()
        fieldsList.getData().size() == 1
        fieldsList.getData().get(0) != null

        def data = fieldsList.getData().get(0)
        data != null
        data.getModality() == DynamicFieldsCapitalizationListData.ModalityEnum.TRADICIONAL
        data.getQuoteRequiredFields().get(0) == "Dependents"
        data.getFields().get(0) != null
        data.getFields().get(0).getName() == "Dependents"
        data.getFields().get(0).getCategory() == Fields.CategoryEnum.RELATIONSHIPINFO
        data.getFields().get(0).getItems().get(0) != null
        data.getFields().get(0).getItems().get(0).getFormat() == FieldsArray.FormatEnum.INT32
    }
}
