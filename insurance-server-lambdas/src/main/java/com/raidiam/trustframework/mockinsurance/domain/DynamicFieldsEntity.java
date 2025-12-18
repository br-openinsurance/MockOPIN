package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "dynamic_fields")
public class DynamicFieldsEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "dynamic_field_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID dynamicFieldId;

    @Column(name = "api")
    private String api;

    public DynamicFieldListData mapDamageAndPersonDTO() {
        return new DynamicFieldListData()
                .api(DynamicFieldListData.ApiEnum.fromValue(this.getApi()))
                .branch(List.of(new DynamicFieldListBranch()
                        .code("0111")
                        .fields(List.of(new Fields()
                                .fieldId(this.getDynamicFieldId().toString())
                                .name("Dependents")
                                .type(Fields.TypeEnum.INTEGER)
                                .category(Fields.CategoryEnum.RELATIONSHIPINFO)
                                .format(Fields.FormatEnum.INT32)
                                .example("2")
                                .maxLength(3)
                                .items(List.of(new FieldsArray()
                                        .fieldId(this.getDynamicFieldId().toString())
                                        .name("Dependents")
                                        .type(FieldsArray.TypeEnum.INTEGER)
                                        .format(FieldsArray.FormatEnum.INT32)
                                        .example("2")
                                        .maxLength(3)))))
                        .quoteRequiredFields(List.of("Dependents"))));
    }

    public DynamicFieldsCapitalizationListData mapCapitalizationTitleDTO() {
        return new DynamicFieldsCapitalizationListData()
                .fields(List.of(new Fields()
                        .fieldId(this.getDynamicFieldId().toString())
                        .name("Dependents")
                        .type(Fields.TypeEnum.INTEGER)
                        .category(Fields.CategoryEnum.RELATIONSHIPINFO)
                        .format(Fields.FormatEnum.INT32)
                        .example("2")
                        .maxLength(3)
                        .items(List.of(new FieldsArray()
                                .fieldId(this.getDynamicFieldId().toString())
                                .name("Dependents")
                                .type(FieldsArray.TypeEnum.INTEGER)
                                .format(FieldsArray.FormatEnum.INT32)
                                .example("2")
                                .maxLength(3)))))
                .modality(DynamicFieldsCapitalizationListData.ModalityEnum.TRADICIONAL)
                .quoteRequiredFields(List.of("Dependents"));
    }

    public DynamicFieldListV2Data mapDamageAndPersonDTOV2() {
        return new DynamicFieldListV2Data()
                .api(DynamicFieldListV2Data.ApiEnum.fromValue(this.getApi()))
                .branch(List.of(new DynamicFieldListV2Branch()
                        .code("0111")
                        .fields(List.of(new FieldsV2()
                                .fieldId(this.getDynamicFieldId().toString())
                                .name("Dependents")
                                .type(FieldsV2.TypeEnum.INTEGER)
                                .category(FieldsV2.CategoryEnum.RELATIONSHIPINFO)
                                .format(FieldsV2.FormatEnum.INT32)
                                .example("2")
                                .maxLength(3)
                                .items(List.of(new FieldsArrayV2()
                                        .type(FieldsArrayV2.TypeEnum.INTEGER)
                                        .format(FieldsArrayV2.FormatEnum.INT32)
                                        .example("2")
                                        .maxLength(3)))))
                        .quoteRequiredFields(List.of("Dependents"))));
    }

    public DynamicFieldsCapitalizationListV2Data mapCapitalizationTitleDTOV2() {
        return new DynamicFieldsCapitalizationListV2Data()
                .fields(List.of(new FieldsV2()
                        .fieldId(this.getDynamicFieldId().toString())
                        .name("Dependents")
                        .type(FieldsV2.TypeEnum.INTEGER)
                        .category(FieldsV2.CategoryEnum.RELATIONSHIPINFO)
                        .format(FieldsV2.FormatEnum.INT32)
                        .example("2")
                        .maxLength(3)
                        .items(List.of(new FieldsArrayV2()
                                .type(FieldsArrayV2.TypeEnum.INTEGER)
                                .format(FieldsArrayV2.FormatEnum.INT32)
                                .example("2")
                                .maxLength(3)))))
                .modality(DynamicFieldsCapitalizationListV2Data.ModalityEnum.TRADICIONAL)
                .quoteRequiredFields(List.of("Dependents"));
    }
}
