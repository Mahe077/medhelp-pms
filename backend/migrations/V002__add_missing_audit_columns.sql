-- Migration to add missing audit columns to prescription_schema tables
-- These columns are required by BaseEntity

-- Medications
ALTER TABLE prescription_schema.medications ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.medications ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.medications ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE prescription_schema.medications ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;

-- Prescriptions
ALTER TABLE prescription_schema.prescriptions ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescriptions ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescriptions ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;

-- Prescription Items
ALTER TABLE prescription_schema.prescription_items ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_items ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE prescription_schema.prescription_items ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_items ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE prescription_schema.prescription_items ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;

-- Prescription Refills
ALTER TABLE prescription_schema.prescription_refills ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_refills ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE prescription_schema.prescription_refills ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_refills ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE prescription_schema.prescription_refills ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;

-- Drug Interactions
ALTER TABLE prescription_schema.drug_interactions ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.drug_interactions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE prescription_schema.drug_interactions ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.drug_interactions ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE prescription_schema.drug_interactions ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;

-- Prescription Validations
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES user_schema.users(id);
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE prescription_schema.prescription_validations ADD COLUMN IF NOT EXISTS version INTEGER DEFAULT 1;
