UPDATE housing_policy_insured_objects        SET type = 'CONTRATO' WHERE type = 'AUTOMOVEL';
UPDATE housing_policy_insured_object_coverages SET branch = '0977' WHERE branch = '0111';
UPDATE housing_policy_claim_coverages        SET branch = '0977' WHERE branch = '0111';
UPDATE housing_policy_premium_coverages      SET branch = '0977' WHERE branch = '0111';

UPDATE rural_policy_insured_object_coverages SET branch = '1133' WHERE branch = '0111';
UPDATE rural_policy_coverages                SET branch = '1133' WHERE branch = '0111';
UPDATE rural_policy_claim_coverages          SET branch = '1133' WHERE branch = '0111';
UPDATE rural_policy_premium_coverages        SET branch = '1133' WHERE branch = '0111';

UPDATE financial_risk_policy_insured_object_coverages SET branch = '0775' WHERE branch = '0111';
UPDATE financial_risk_policy_coverages                SET branch = '0775' WHERE branch = '0111';
UPDATE financial_risk_policy_claim_coverages          SET branch = '0775' WHERE branch = '0111';
UPDATE financial_risk_policy_premium_coverages        SET branch = '0775' WHERE branch = '0111';
