ALTER TABLE capitalization_title_plan_subscribers
ADD COLUMN subscriber_flag_post_code VARCHAR,
ADD COLUMN subscriber_district_name VARCHAR,
ADD COLUMN subscriber_town_code VARCHAR;

ALTER TABLE capitalization_title_plan_subscribers_aud
ADD COLUMN subscriber_flag_post_code VARCHAR,
ADD COLUMN subscriber_district_name VARCHAR,
ADD COLUMN subscriber_town_code VARCHAR;

ALTER TABLE capitalization_title_plan_holders
ADD COLUMN holder_flag_post_code VARCHAR,
ADD COLUMN holder_district_name VARCHAR,
ADD COLUMN holder_town_code VARCHAR;

ALTER TABLE capitalization_title_plan_holders_aud
ADD COLUMN holder_flag_post_code VARCHAR,
ADD COLUMN holder_district_name VARCHAR,
ADD COLUMN holder_town_code VARCHAR;
