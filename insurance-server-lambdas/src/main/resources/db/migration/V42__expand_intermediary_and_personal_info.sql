ALTER TABLE personal_info
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR;

ALTER TABLE personal_info_aud
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR;

ALTER TABLE intermediaries
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR,
ADD COLUMN address_additional_info VARCHAR;

ALTER TABLE intermediaries_aud
ADD COLUMN flag_post_code VARCHAR,
ADD COLUMN district_name VARCHAR,
ADD COLUMN town_code VARCHAR,
ADD COLUMN address_additional_info VARCHAR;
