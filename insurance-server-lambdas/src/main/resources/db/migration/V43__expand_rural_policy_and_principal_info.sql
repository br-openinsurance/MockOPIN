ALTER TABLE rural_policy_claims
ADD COLUMN survey_address_complementary_info VARCHAR;

ALTER TABLE rural_policy_claims_aud
ADD COLUMN survey_address_complementary_info VARCHAR;

ALTER TABLE rural_policy_branch_insured_objects
ADD COLUMN survey_address_complementary_info VARCHAR;

ALTER TABLE rural_policy_branch_insured_objects_aud
ADD COLUMN survey_address_complementary_info VARCHAR;

ALTER TABLE principal_info
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR;

ALTER TABLE principal_info_aud
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR;
