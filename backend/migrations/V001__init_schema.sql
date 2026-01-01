-- Flyway Migration V001__init_schema.sql
-- This migration creates all schemas and tables for the Pharmacy Management System.

-- 1. User Schema
CREATE SCHEMA IF NOT EXISTS user_schema;

CREATE TABLE IF NOT EXISTS user_schema.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    license_number VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS user_schema.roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.role_permissions (
    role_id UUID REFERENCES user_schema.roles(id) ON DELETE CASCADE,
    permission_id UUID REFERENCES user_schema.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES user_schema.users(id) ON DELETE CASCADE,
    refresh_token VARCHAR(500) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_users_email ON user_schema.users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON user_schema.users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON user_schema.users(role);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_schema.user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_refresh_token ON user_schema.user_sessions(refresh_token);

-- 2. Patient Schema
CREATE SCHEMA IF NOT EXISTS patient_schema;

CREATE TABLE IF NOT EXISTS patient_schema.patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    ssn_last_four VARCHAR(4),
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(255),
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    country VARCHAR(2) DEFAULT 'US',
    language_preference VARCHAR(10) DEFAULT 'en',
    communication_preference VARCHAR(20) DEFAULT 'sms',
    allow_generic_substitution BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID REFERENCES user_schema.users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS patient_schema.patient_allergies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    allergen_type VARCHAR(50) NOT NULL,
    allergen_name VARCHAR(255) NOT NULL,
    reaction VARCHAR(255),
    severity VARCHAR(20),
    notes TEXT,
    onset_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS patient_schema.patient_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    condition_name VARCHAR(255) NOT NULL,
    icd_10_code VARCHAR(10),
    diagnosed_date DATE,
    is_active BOOLEAN DEFAULT true,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id)
);

CREATE TABLE IF NOT EXISTS patient_schema.patient_insurance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    insurance_provider VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(50),
    policy_number VARCHAR(100) NOT NULL,
    group_number VARCHAR(100),
    bin_number VARCHAR(20),
    pcn_number VARCHAR(20),
    cardholder_name VARCHAR(255),
    relationship_to_cardholder VARCHAR(50),
    effective_date DATE NOT NULL,
    expiration_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS patient_schema.patient_prescribers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    prescriber_name VARCHAR(255) NOT NULL,
    prescriber_npi VARCHAR(10) NOT NULL,
    prescriber_dea VARCHAR(20),
    specialty VARCHAR(100),
    phone VARCHAR(20),
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_patients_patient_number ON patient_schema.patients(patient_number);
CREATE INDEX IF NOT EXISTS idx_patients_phone ON patient_schema.patients(phone_primary);
CREATE INDEX IF NOT EXISTS idx_patients_dob ON patient_schema.patients(date_of_birth);
CREATE INDEX IF NOT EXISTS idx_patients_name ON patient_schema.patients(last_name, first_name);
CREATE INDEX IF NOT EXISTS idx_patient_allergies_patient_id ON patient_schema.patient_allergies(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_insurance_patient_id ON patient_schema.patient_insurance(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_prescribers_patient_id ON patient_schema.patient_prescribers(patient_id);

-- 3. Prescription Schema
CREATE SCHEMA IF NOT EXISTS prescription_schema;

CREATE TABLE IF NOT EXISTS prescription_schema.medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ndc_code VARCHAR(11) UNIQUE NOT NULL,
    drug_name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    brand_name VARCHAR(255),
    strength VARCHAR(50),
    dosage_form VARCHAR(50),
    route VARCHAR(50),
    manufacturer VARCHAR(255),
    is_controlled_substance BOOLEAN DEFAULT false,
    dea_schedule VARCHAR(5),
    is_generic BOOLEAN DEFAULT false,
    therapeutic_class VARCHAR(100),
    pharmacologic_class VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    discontinuation_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prescription_schema.prescriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_number VARCHAR(20) UNIQUE NOT NULL,
    patient_id UUID NOT NULL,
    prescriber_name VARCHAR(255) NOT NULL,
    prescriber_npi VARCHAR(10) NOT NULL,
    prescriber_dea VARCHAR(20),
    prescriber_phone VARCHAR(20),
    prescriber_address TEXT,
    prescription_date DATE NOT NULL,
    written_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL,
    external_rx_number VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'received',
    received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    received_by UUID REFERENCES user_schema.users(id),
    validated_at TIMESTAMP WITH TIME ZONE,
    validated_by UUID REFERENCES user_schema.users(id),
    filled_at TIMESTAMP WITH TIME ZONE,
    filled_by UUID REFERENCES user_schema.users(id),
    dispensed_at TIMESTAMP WITH TIME ZONE,
    dispensed_by UUID REFERENCES user_schema.users(id),
    is_stat BOOLEAN DEFAULT false,
    requires_counseling BOOLEAN DEFAULT true,
    counseling_completed BOOLEAN DEFAULT false,
    counseling_declined BOOLEAN DEFAULT false,
    notes TEXT,
    rejection_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS prescription_schema.prescription_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    medication_id UUID REFERENCES prescription_schema.medications(id),
    written_medication_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    quantity_unit VARCHAR(20) NOT NULL,
    days_supply INTEGER NOT NULL,
    sig TEXT NOT NULL,
    refills_authorized INTEGER NOT NULL DEFAULT 0,
    refills_remaining INTEGER NOT NULL DEFAULT 0,
    substitution_allowed BOOLEAN DEFAULT true,
    daw_code VARCHAR(2),
    dispensed_medication_id UUID REFERENCES prescription_schema.medications(id),
    dispensed_quantity DECIMAL(10,2),
    dispensed_ndc VARCHAR(11),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prescription_schema.prescription_refills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    prescription_item_id UUID REFERENCES prescription_schema.prescription_items(id),
    refill_number INTEGER NOT NULL,
    medication_id UUID REFERENCES prescription_schema.medications(id),
    ndc_code VARCHAR(11) NOT NULL,
    quantity_dispensed DECIMAL(10,2) NOT NULL,
    days_supply INTEGER NOT NULL,
    filled_date DATE NOT NULL,
    filled_by UUID REFERENCES user_schema.users(id),
    billed_amount DECIMAL(10,2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prescription_schema.drug_interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_1_id UUID REFERENCES prescription_schema.medications(id),
    medication_2_id UUID REFERENCES prescription_schema.medications(id),
    interaction_type VARCHAR(50),
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    management_recommendation TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CHECK (medication_1_id < medication_2_id)
);

CREATE TABLE IF NOT EXISTS prescription_schema.prescription_validations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    validation_type VARCHAR(50) NOT NULL,
    validation_status VARCHAR(20) NOT NULL,
    details JSONB,
    performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_by UUID REFERENCES user_schema.users(id)
);

CREATE INDEX IF NOT EXISTS idx_prescriptions_patient_id ON prescription_schema.prescriptions(patient_id);
CREATE INDEX IF NOT EXISTS idx_prescriptions_status ON prescription_schema.prescriptions(status);
CREATE INDEX IF NOT EXISTS idx_prescriptions_prescription_number ON prescription_schema.prescriptions(prescription_number);
CREATE INDEX IF NOT EXISTS idx_prescriptions_prescriber_npi ON prescription_schema.prescriptions(prescriber_npi);
CREATE INDEX IF NOT EXISTS idx_prescription_items_prescription_id ON prescription_schema.prescription_items(prescription_id);
CREATE INDEX IF NOT EXISTS idx_prescription_items_medication_id ON prescription_schema.prescription_items(medication_id);
CREATE INDEX IF NOT EXISTS idx_prescription_refills_prescription_id ON prescription_schema.prescription_refills(prescription_id);
CREATE INDEX IF NOT EXISTS idx_medications_ndc_code ON prescription_schema.medications(ndc_code);
CREATE INDEX IF NOT EXISTS idx_medications_drug_name ON prescription_schema.medications(drug_name);
CREATE INDEX IF NOT EXISTS idx_drug_interactions_medication_ids ON prescription_schema.drug_interactions(medication_1_id, medication_2_id);

-- 4. Inventory Schema
CREATE SCHEMA IF NOT EXISTS inventory_schema;

CREATE TABLE IF NOT EXISTS inventory_schema.inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_id UUID NOT NULL,
    ndc_code VARCHAR(11) NOT NULL,
    bin_location VARCHAR(50),
    requires_refrigeration BOOLEAN DEFAULT false,
    quantity_on_hand DECIMAL(10,2) NOT NULL DEFAULT 0,
    quantity_unit VARCHAR(20) NOT NULL,
    reorder_point DECIMAL(10,2) NOT NULL,
    reorder_quantity DECIMAL(10,2) NOT NULL,
    economic_order_quantity DECIMAL(10,2),
    unit_cost DECIMAL(10,4),
    average_wholesale_price DECIMAL(10,4),
    is_active BOOLEAN DEFAULT true,
    last_counted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS inventory_schema.inventory_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    batch_number VARCHAR(50) NOT NULL,
    expiration_date DATE NOT NULL,
    quantity_received DECIMAL(10,2) NOT NULL,
    quantity_remaining DECIMAL(10,2) NOT NULL,
    unit_cost DECIMAL(10,4),
    received_date DATE NOT NULL,
    received_by UUID REFERENCES user_schema.users(id),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_schema.suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    payment_terms VARCHAR(100),
    minimum_order_amount DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_schema.purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    po_number VARCHAR(20) UNIQUE NOT NULL,
    supplier_id UUID REFERENCES inventory_schema.suppliers(id),
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    subtotal DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    shipping_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_schema.purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id UUID REFERENCES inventory_schema.purchase_orders(id) ON DELETE CASCADE,
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    ndc_code VARCHAR(11) NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    quantity_ordered DECIMAL(10,2) NOT NULL,
    quantity_received DECIMAL(10,2) DEFAULT 0,
    unit_price DECIMAL(10,4) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_schema.inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    batch_id UUID REFERENCES inventory_schema.inventory_batches(id),
    transaction_type VARCHAR(50) NOT NULL,
    quantity_change DECIMAL(10,2) NOT NULL,
    quantity_before DECIMAL(10,2) NOT NULL,
    quantity_after DECIMAL(10,2) NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    reason TEXT,
    transaction_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_by UUID REFERENCES user_schema.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_schema.stock_adjustments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    batch_id UUID REFERENCES inventory_schema.inventory_batches(id),
    adjustment_type VARCHAR(50) NOT NULL,
    quantity_expected DECIMAL(10,2) NOT NULL,
    quantity_actual DECIMAL(10,2) NOT NULL,
    quantity_difference DECIMAL(10,2) NOT NULL,
    reason TEXT NOT NULL,
    requires_approval BOOLEAN DEFAULT true,
    approved_by UUID REFERENCES user_schema.users(id),
    approved_at TIMESTAMP WITH TIME ZONE,
    adjustment_date DATE NOT NULL,
    performed_by UUID REFERENCES user_schema.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_inventory_items_medication_id ON inventory_schema.inventory_items(medication_id);
CREATE INDEX IF NOT EXISTS idx_inventory_items_ndc ON inventory_schema.inventory_items(ndc_code);
CREATE INDEX IF NOT EXISTS idx_inventory_batches_inventory_item_id ON inventory_schema.inventory_batches(inventory_item_id);
CREATE INDEX IF NOT EXISTS idx_inventory_batches_expiration_date ON inventory_schema.inventory_batches(expiration_date);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_supplier_id ON inventory_schema.purchase_orders(supplier_id);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_status ON inventory_schema.purchase_orders(status);
CREATE INDEX IF NOT EXISTS idx_inventory_transactions_inventory_item_id ON inventory_schema.inventory_transactions(inventory_item_id);
CREATE INDEX IF NOT EXISTS idx_inventory_transactions_transaction_date ON inventory_schema.inventory_transactions(transaction_date);

-- 5. Billing Schema
CREATE SCHEMA IF NOT EXISTS billing_schema;

CREATE TABLE IF NOT EXISTS billing_schema.invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(20) UNIQUE NOT NULL,
    prescription_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    insurance_payment DECIMAL(10,2) DEFAULT 0,
    patient_copay DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS billing_schema.invoice_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID REFERENCES billing_schema.invoices(id) ON DELETE CASCADE,
    prescription_item_id UUID,
    item_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    quantity DECIMAL(10,2) DEFAULT 1,
    unit_price DECIMAL(10,4) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    ndc_code VARCHAR(11),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS billing_schema.payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_number VARCHAR(20) UNIQUE NOT NULL,
    invoice_id UUID REFERENCES billing_schema.invoices(id),
    patient_id UUID NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_id VARCHAR(100),
    card_last_four VARCHAR(4),
    check_number VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'completed',
    notes TEXT,
    processed_by UUID REFERENCES user_schema.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS billing_schema.insurance_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    prescription_id UUID NOT NULL,
    patient_insurance_id UUID NOT NULL,
    submission_date DATE NOT NULL,
    submitted_amount DECIMAL(10,2) NOT NULL,
    approved_amount DECIMAL(10,2),
    patient_responsibility DECIMAL(10,2),
    status VARCHAR(50) NOT NULL DEFAULT 'submitted',
    rejection_code VARCHAR(20),
    rejection_reason TEXT,
    response_date DATE,
    payment_date DATE,
    external_claim_id VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS billing_schema.pricing_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    conditions JSONB NOT NULL,
    pricing_method VARCHAR(50) NOT NULL,
    pricing_value DECIMAL(10,4) NOT NULL,
    priority INTEGER DEFAULT 0,
    effective_date DATE NOT NULL,
    expiration_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id)
);

CREATE TABLE IF NOT EXISTS billing_schema.discounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,4) NOT NULL,
    max_uses INTEGER,
    times_used INTEGER DEFAULT 0,
    max_uses_per_patient INTEGER DEFAULT 1,
    minimum_purchase_amount DECIMAL(10,2),
    applicable_medications JSONB,
    effective_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_invoices_prescription_id ON billing_schema.invoices(prescription_id);
CREATE INDEX IF NOT EXISTS idx_invoices_patient_id ON billing_schema.invoices(patient_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON billing_schema.invoices(status);
CREATE INDEX IF NOT EXISTS idx_payments_invoice_id ON billing_schema.payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_insurance_claims_prescription_id ON billing_schema.insurance_claims(prescription_id);
CREATE INDEX IF NOT EXISTS idx_insurance_claims_status ON billing_schema.insurance_claims(status);
CREATE INDEX IF NOT EXISTS idx_pricing_rules_effective_expiration ON billing_schema.pricing_rules(effective_date, expiration_date);

-- 6. Notification Schema
CREATE SCHEMA IF NOT EXISTS notification_schema;

CREATE TABLE IF NOT EXISTS notification_schema.notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name VARCHAR(100) UNIQUE NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    event_trigger VARCHAR(100) NOT NULL,
    subject VARCHAR(255),
    body_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_schema.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    template_id UUID REFERENCES notification_schema.notification_templates(id),
    notification_type VARCHAR(50) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    external_id VARCHAR(255),
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    reference_type VARCHAR(50),
    reference_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_patient_id ON notification_schema.notifications(patient_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notification_schema.notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notification_schema.notifications(created_at);

-- 7. Audit Schema
CREATE SCHEMA IF NOT EXISTS audit_schema;

CREATE TABLE IF NOT EXISTS audit_schema.domain_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(100) NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_data JSONB NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id UUID REFERENCES user_schema.users(id),
    sequence_number BIGSERIAL
);

CREATE TABLE IF NOT EXISTS audit_schema.access_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES user_schema.users(id),
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id UUID NOT NULL,
    ip_address INET,
    user_agent TEXT,
    accessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_domain_events_aggregate ON audit_schema.domain_events(aggregate_type, aggregate_id);
CREATE INDEX IF NOT EXISTS idx_domain_events_occurred_at ON audit_schema.domain_events(occurred_at);
CREATE INDEX IF NOT EXISTS idx_access_logs_user_id ON audit_schema.access_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_access_logs_resource ON audit_schema.access_logs(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_access_logs_accessed_at ON audit_schema.access_logs(accessed_at);

-- End of migration
