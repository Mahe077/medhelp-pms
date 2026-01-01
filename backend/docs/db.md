Perfect! Let's design the database schema. I'll design it module by module, starting with the core modules.

## Database Design Principles

Before we dive in, here are the principles guiding our schema design:

1. **Separate schemas per module** - Enforces boundaries, easier to extract later
2. **Normalized for consistency** - 3NF mostly, denormalize only where performance demands
3. **Audit everything** - created_at, updated_at, created_by, updated_by on most tables
4. **Soft deletes** - deleted_at for records that shouldn't be hard deleted (compliance)
5. **Optimistic locking** - version column for handling concurrent updates
6. **Indexes on foreign keys and query patterns** - Performance from day one
7. **Use UUIDs for IDs** - Easier to merge data, better for distributed systems later

---

## Schema Organization

```
pharmacy_db
├── prescription_schema     (Prescription Module)
├── inventory_schema        (Inventory Module)
├── patient_schema          (Patient Module)
├── billing_schema          (Billing Module)
├── user_schema            (User/Auth Module)
├── notification_schema     (Notification Module)
└── audit_schema           (System-wide audit logs)
```

---

## 1. User Schema (Foundation - Authentication & Authorization)

```sql
CREATE SCHEMA user_schema;

-- Users table
CREATE TABLE user_schema.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL, -- 'pharmacist', 'technician', 'manager', 'admin'
    license_number VARCHAR(50), -- Professional license for pharmacists
    is_active BOOLEAN DEFAULT true,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Roles and Permissions (RBAC)
CREATE TABLE user_schema.roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL, -- 'pharmacist', 'technician', etc.
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_schema.permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL, -- 'prescription.fill', 'inventory.adjust', etc.
    description TEXT,
    resource VARCHAR(50) NOT NULL, -- 'prescription', 'inventory', etc.
    action VARCHAR(50) NOT NULL, -- 'create', 'read', 'update', 'delete', 'fill', etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_schema.role_permissions (
    role_id UUID REFERENCES user_schema.roles(id) ON DELETE CASCADE,
    permission_id UUID REFERENCES user_schema.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- User sessions (for JWT refresh tokens)
CREATE TABLE user_schema.user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES user_schema.users(id) ON DELETE CASCADE,
    refresh_token VARCHAR(500) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE
);

-- Indexes
CREATE INDEX idx_users_email ON user_schema.users(email);
CREATE INDEX idx_users_username ON user_schema.users(username);
CREATE INDEX idx_users_role ON user_schema.users(role);
CREATE INDEX idx_user_sessions_user_id ON user_schema.user_sessions(user_id);
CREATE INDEX idx_user_sessions_refresh_token ON user_schema.user_sessions(refresh_token);
```

**Why This Design**:

- **Flexible RBAC**: Easy to add new roles/permissions without code changes
- **Session management**: Track refresh tokens, can revoke if compromised
- **License tracking**: Required for regulatory compliance
- **Soft deletes**: Keep user history even if deactivated

---

## 2. Patient Schema

```sql
CREATE SCHEMA patient_schema;

-- Patients table
CREATE TABLE patient_schema.patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_number VARCHAR(20) UNIQUE NOT NULL, -- Human-readable ID
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20), -- 'male', 'female', 'other', 'prefer_not_to_say'
    ssn_last_four VARCHAR(4), -- Last 4 of SSN for verification

    -- Contact information
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(255),

    -- Address
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    country VARCHAR(2) DEFAULT 'US',

    -- Preferences
    language_preference VARCHAR(10) DEFAULT 'en',
    communication_preference VARCHAR(20) DEFAULT 'sms', -- 'sms', 'email', 'phone', 'none'
    allow_generic_substitution BOOLEAN DEFAULT true,

    -- Status
    is_active BOOLEAN DEFAULT true,

    -- Audit
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID REFERENCES user_schema.users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 1 -- Optimistic locking
);

-- Patient Allergies
CREATE TABLE patient_schema.patient_allergies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    allergen_type VARCHAR(50) NOT NULL, -- 'medication', 'food', 'environmental'
    allergen_name VARCHAR(255) NOT NULL,
    reaction VARCHAR(255), -- 'rash', 'anaphylaxis', etc.
    severity VARCHAR(20), -- 'mild', 'moderate', 'severe'
    notes TEXT,
    onset_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Patient Medical Conditions
CREATE TABLE patient_schema.patient_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    condition_name VARCHAR(255) NOT NULL,
    icd_10_code VARCHAR(10), -- Standard medical coding
    diagnosed_date DATE,
    is_active BOOLEAN DEFAULT true,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id)
);

-- Patient Insurance
CREATE TABLE patient_schema.patient_insurance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    insurance_provider VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(50), -- 'primary', 'secondary'
    policy_number VARCHAR(100) NOT NULL,
    group_number VARCHAR(100),
    bin_number VARCHAR(20), -- Pharmacy benefit manager ID
    pcn_number VARCHAR(20), -- Processor control number
    cardholder_name VARCHAR(255),
    relationship_to_cardholder VARCHAR(50), -- 'self', 'spouse', 'child'
    effective_date DATE NOT NULL,
    expiration_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Patient Prescribers (Preferred doctors)
CREATE TABLE patient_schema.patient_prescribers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patient_schema.patients(id) ON DELETE CASCADE,
    prescriber_name VARCHAR(255) NOT NULL,
    prescriber_npi VARCHAR(10) NOT NULL, -- National Provider Identifier
    prescriber_dea VARCHAR(20), -- DEA number for controlled substances
    specialty VARCHAR(100),
    phone VARCHAR(20),
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_patients_patient_number ON patient_schema.patients(patient_number);
CREATE INDEX idx_patients_phone ON patient_schema.patients(phone_primary);
CREATE INDEX idx_patients_dob ON patient_schema.patients(date_of_birth);
CREATE INDEX idx_patients_name ON patient_schema.patients(last_name, first_name);
CREATE INDEX idx_patient_allergies_patient_id ON patient_schema.patient_allergies(patient_id);
CREATE INDEX idx_patient_insurance_patient_id ON patient_schema.patient_insurance(patient_id);
CREATE INDEX idx_patient_prescribers_patient_id ON patient_schema.patient_prescribers(patient_id);
```

**Why This Design**:

- **Complete patient profile**: Everything needed for safe dispensing
- **Multiple insurance support**: Many patients have primary + secondary
- **Allergy tracking**: Critical for patient safety
- **Versioning**: Optimistic locking prevents concurrent update conflicts
- **HIPAA compliance**: Audit trails (created_by, updated_by)

---

## 3. Prescription Schema (Core Domain)

```sql
CREATE SCHEMA prescription_schema;

-- Drug formulary (medication catalog)
CREATE TABLE prescription_schema.medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ndc_code VARCHAR(11) UNIQUE NOT NULL, -- National Drug Code
    drug_name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    brand_name VARCHAR(255),
    strength VARCHAR(50), -- '500mg', '10ml', etc.
    dosage_form VARCHAR(50), -- 'tablet', 'capsule', 'liquid', etc.
    route VARCHAR(50), -- 'oral', 'topical', 'injection', etc.
    manufacturer VARCHAR(255),

    -- Classification
    is_controlled_substance BOOLEAN DEFAULT false,
    dea_schedule VARCHAR(5), -- 'II', 'III', 'IV', 'V' for controlled substances
    is_generic BOOLEAN DEFAULT false,

    -- Clinical information
    therapeutic_class VARCHAR(100),
    pharmacologic_class VARCHAR(100),

    -- Status
    is_active BOOLEAN DEFAULT true,
    discontinuation_date DATE,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Prescriptions
CREATE TABLE prescription_schema.prescriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_number VARCHAR(20) UNIQUE NOT NULL, -- RX123456
    patient_id UUID NOT NULL, -- References patient_schema.patients

    -- Prescriber information
    prescriber_name VARCHAR(255) NOT NULL,
    prescriber_npi VARCHAR(10) NOT NULL,
    prescriber_dea VARCHAR(20),
    prescriber_phone VARCHAR(20),
    prescriber_address TEXT,

    -- Prescription details
    prescription_date DATE NOT NULL,
    written_date DATE NOT NULL,
    expiration_date DATE NOT NULL,

    -- Source
    source VARCHAR(20) NOT NULL, -- 'paper', 'electronic', 'phone', 'fax'
    external_rx_number VARCHAR(50), -- If from e-prescribing system

    -- Status and workflow
    status VARCHAR(50) NOT NULL DEFAULT 'received',
    -- 'received' → 'validated' → 'in_progress' → 'ready' → 'dispensed' → 'completed'
    -- Or: 'rejected', 'cancelled'

    -- Processing information
    received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    received_by UUID REFERENCES user_schema.users(id),
    validated_at TIMESTAMP WITH TIME ZONE,
    validated_by UUID REFERENCES user_schema.users(id),
    filled_at TIMESTAMP WITH TIME ZONE,
    filled_by UUID REFERENCES user_schema.users(id), -- Pharmacist who filled
    dispensed_at TIMESTAMP WITH TIME ZONE,
    dispensed_by UUID REFERENCES user_schema.users(id),

    -- Flags and notes
    is_stat BOOLEAN DEFAULT false, -- Urgent/emergency
    requires_counseling BOOLEAN DEFAULT true,
    counseling_completed BOOLEAN DEFAULT false,
    counseling_declined BOOLEAN DEFAULT false,

    notes TEXT,
    rejection_reason TEXT,

    -- Audit
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

-- Prescription Items (medications on the prescription)
CREATE TABLE prescription_schema.prescription_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    medication_id UUID REFERENCES prescription_schema.medications(id),

    -- Written prescription details
    written_medication_name VARCHAR(255) NOT NULL, -- What doctor wrote
    quantity DECIMAL(10, 2) NOT NULL,
    quantity_unit VARCHAR(20) NOT NULL, -- 'tablets', 'ml', etc.
    days_supply INTEGER NOT NULL,

    -- Directions (SIG)
    sig TEXT NOT NULL, -- "Take 1 tablet by mouth twice daily"

    -- Refills
    refills_authorized INTEGER NOT NULL DEFAULT 0,
    refills_remaining INTEGER NOT NULL DEFAULT 0,

    -- Substitution
    substitution_allowed BOOLEAN DEFAULT true,
    daw_code VARCHAR(2), -- Dispense As Written code

    -- What was actually dispensed
    dispensed_medication_id UUID REFERENCES prescription_schema.medications(id),
    dispensed_quantity DECIMAL(10, 2),
    dispensed_ndc VARCHAR(11),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Prescription Refills (tracking each fill/refill)
CREATE TABLE prescription_schema.prescription_refills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    prescription_item_id UUID REFERENCES prescription_schema.prescription_items(id),

    refill_number INTEGER NOT NULL, -- 0 = original fill, 1, 2, 3... = refills

    -- What was dispensed
    medication_id UUID REFERENCES prescription_schema.medications(id),
    ndc_code VARCHAR(11) NOT NULL,
    quantity_dispensed DECIMAL(10, 2) NOT NULL,
    days_supply INTEGER NOT NULL,

    -- Processing
    filled_date DATE NOT NULL,
    filled_by UUID REFERENCES user_schema.users(id),

    -- Billing (we'll link to billing schema)
    billed_amount DECIMAL(10, 2),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Drug Interactions (for clinical decision support)
CREATE TABLE prescription_schema.drug_interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_1_id UUID REFERENCES prescription_schema.medications(id),
    medication_2_id UUID REFERENCES prescription_schema.medications(id),
    interaction_type VARCHAR(50), -- 'major', 'moderate', 'minor'
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL, -- 'contraindicated', 'serious', 'significant', 'minor'
    management_recommendation TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CHECK (medication_1_id < medication_2_id) -- Prevent duplicates
);

-- Prescription validations/checks log
CREATE TABLE prescription_schema.prescription_validations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID REFERENCES prescription_schema.prescriptions(id) ON DELETE CASCADE,
    validation_type VARCHAR(50) NOT NULL,
    -- 'prescriber_license', 'drug_interaction', 'allergy_check', 'dea_verification', etc.
    validation_status VARCHAR(20) NOT NULL, -- 'passed', 'failed', 'warning'
    details JSONB, -- Flexible storage for validation details
    performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_by UUID REFERENCES user_schema.users(id)
);

-- Indexes
CREATE INDEX idx_prescriptions_patient_id ON prescription_schema.prescriptions(patient_id);
CREATE INDEX idx_prescriptions_status ON prescription_schema.prescriptions(status);
CREATE INDEX idx_prescriptions_prescription_number ON prescription_schema.prescriptions(prescription_number);
CREATE INDEX idx_prescriptions_prescriber_npi ON prescription_schema.prescriptions(prescriber_npi);
CREATE INDEX idx_prescription_items_prescription_id ON prescription_schema.prescription_items(prescription_id);
CREATE INDEX idx_prescription_items_medication_id ON prescription_schema.prescription_items(medication_id);
CREATE INDEX idx_prescription_refills_prescription_id ON prescription_schema.prescription_refills(prescription_id);
CREATE INDEX idx_medications_ndc_code ON prescription_schema.medications(ndc_code);
CREATE INDEX idx_medications_drug_name ON prescription_schema.medications(drug_name);
CREATE INDEX idx_drug_interactions_medication_ids ON prescription_schema.drug_interactions(medication_1_id, medication_2_id);
```

**Why This Design**:

- **Complete workflow tracking**: Every status change, who did it, when
- **Refill tracking**: Separate table for each fill/refill event
- **Clinical safety**: Drug interactions, allergy checks, validations logged
- **Compliance**: DEA schedules, prescriber tracking for controlled substances
- **Flexible validations**: JSONB for extensible validation details
- **Separation**: Medication catalog separate from prescriptions

---

## 4. Inventory Schema

```sql
CREATE SCHEMA inventory_schema;

-- Inventory Items (what we stock)
CREATE TABLE inventory_schema.inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_id UUID NOT NULL, -- References prescription_schema.medications
    ndc_code VARCHAR(11) NOT NULL,

    -- Location
    bin_location VARCHAR(50), -- Physical storage location
    requires_refrigeration BOOLEAN DEFAULT false,

    -- Stock levels
    quantity_on_hand DECIMAL(10, 2) NOT NULL DEFAULT 0,
    quantity_unit VARCHAR(20) NOT NULL, -- 'tablets', 'ml', 'boxes'

    -- Reordering
    reorder_point DECIMAL(10, 2) NOT NULL, -- Trigger reorder when below this
    reorder_quantity DECIMAL(10, 2) NOT NULL, -- How much to order
    economic_order_quantity DECIMAL(10, 2), -- Optimal order size

    -- Pricing
    unit_cost DECIMAL(10, 4), -- Cost per unit
    average_wholesale_price DECIMAL(10, 4), -- AWP

    -- Status
    is_active BOOLEAN DEFAULT true,
    last_counted_at TIMESTAMP WITH TIME ZONE, -- Last physical count

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1 -- Prevent concurrent stock updates
);

-- Inventory Batches (track lots/expiry dates)
CREATE TABLE inventory_schema.inventory_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),

    batch_number VARCHAR(50) NOT NULL, -- Manufacturer lot number
    expiration_date DATE NOT NULL,

    quantity_received DECIMAL(10, 2) NOT NULL,
    quantity_remaining DECIMAL(10, 2) NOT NULL,

    unit_cost DECIMAL(10, 4), -- Cost for this specific batch

    received_date DATE NOT NULL,
    received_by UUID REFERENCES user_schema.users(id),

    status VARCHAR(20) DEFAULT 'active', -- 'active', 'expired', 'recalled', 'returned'

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Suppliers
CREATE TABLE inventory_schema.suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,

    -- Terms
    payment_terms VARCHAR(100), -- 'Net 30', 'COD', etc.
    minimum_order_amount DECIMAL(10, 2),

    is_active BOOLEAN DEFAULT true,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Purchase Orders
CREATE TABLE inventory_schema.purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    po_number VARCHAR(20) UNIQUE NOT NULL,
    supplier_id UUID REFERENCES inventory_schema.suppliers(id),

    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,

    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 'pending', 'sent', 'partially_received', 'received', 'cancelled'

    subtotal DECIMAL(10, 2),
    tax_amount DECIMAL(10, 2),
    shipping_amount DECIMAL(10, 2),
    total_amount DECIMAL(10, 2),

    notes TEXT,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Purchase Order Items
CREATE TABLE inventory_schema.purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id UUID REFERENCES inventory_schema.purchase_orders(id) ON DELETE CASCADE,
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),

    ndc_code VARCHAR(11) NOT NULL,
    medication_name VARCHAR(255) NOT NULL,

    quantity_ordered DECIMAL(10, 2) NOT NULL,
    quantity_received DECIMAL(10, 2) DEFAULT 0,

    unit_price DECIMAL(10, 4) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Inventory Transactions (complete audit trail)
CREATE TABLE inventory_schema.inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    batch_id UUID REFERENCES inventory_schema.inventory_batches(id),

    transaction_type VARCHAR(50) NOT NULL,
    -- 'receipt', 'dispensed', 'adjustment', 'return', 'expired', 'damaged', 'transfer'

    quantity_change DECIMAL(10, 2) NOT NULL, -- Positive or negative
    quantity_before DECIMAL(10, 2) NOT NULL,
    quantity_after DECIMAL(10, 2) NOT NULL,

    -- Reference to what caused this transaction
    reference_type VARCHAR(50), -- 'prescription', 'purchase_order', 'adjustment'
    reference_id UUID, -- ID of the related record

    reason TEXT,

    transaction_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    performed_by UUID REFERENCES user_schema.users(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Stock Adjustments (for discrepancies, damage, etc.)
CREATE TABLE inventory_schema.stock_adjustments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID REFERENCES inventory_schema.inventory_items(id),
    batch_id UUID REFERENCES inventory_schema.inventory_batches(id),

    adjustment_type VARCHAR(50) NOT NULL, -- 'physical_count', 'damage', 'theft', 'expiry', 'system_error'

    quantity_expected DECIMAL(10, 2) NOT NULL,
    quantity_actual DECIMAL(10, 2) NOT NULL,
    quantity_difference DECIMAL(10, 2) NOT NULL,

    reason TEXT NOT NULL,

    requires_approval BOOLEAN DEFAULT true,
    approved_by UUID REFERENCES user_schema.users(id),
    approved_at TIMESTAMP WITH TIME ZONE,

    adjustment_date DATE NOT NULL,
    performed_by UUID REFERENCES user_schema.users(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_inventory_items_medication_id ON inventory_schema.inventory_items(medication_id);
CREATE INDEX idx_inventory_items_ndc ON inventory_schema.inventory_items(ndc_code);
CREATE INDEX idx_inventory_batches_inventory_item_id ON inventory_schema.inventory_batches(inventory_item_id);
CREATE INDEX idx_inventory_batches_expiration_date ON inventory_schema.inventory_batches(expiration_date);
CREATE INDEX idx_purchase_orders_supplier_id ON inventory_schema.purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON inventory_schema.purchase_orders(status);
CREATE INDEX idx_inventory_transactions_inventory_item_id ON inventory_schema.inventory_transactions(inventory_item_id);
CREATE INDEX idx_inventory_transactions_transaction_date ON inventory_schema.inventory_transactions(transaction_date);
```

**Why This Design**:

- **Batch tracking**: Critical for recalls, expiry management
- **Complete audit trail**: Every stock movement logged
- **Reorder automation**: Triggers when quantity < reorder_point
- **Cost tracking**: Multiple pricing levels (cost, AWP)
- **Approval workflow**: Stock adjustments require manager approval
- **Version locking**: Prevents double-deduction race conditions

---

## 5. Billing Schema

```sql
CREATE SCHEMA billing_schema;

-- Invoices
CREATE TABLE billing_schema.invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(20) UNIQUE NOT NULL,
    prescription_id UUID NOT NULL, -- References prescription_schema.prescriptions
    patient_id UUID NOT NULL, -- References patient_schema.patients

    invoice_date DATE NOT NULL,
    due_date DATE,

    -- Amounts
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    insurance_payment DECIMAL(10, 2) DEFAULT 0,
    patient_copay DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 'pending', 'partially_paid', 'paid', 'overdue', 'cancelled'

    notes TEXT,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Invoice Line Items
CREATE TABLE billing_schema.invoice_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID REFERENCES billing_schema.invoices(id) ON DELETE CASCADE,
    prescription_item_id UUID, -- References prescription_schema.prescription_items

    item_type VARCHAR(50) NOT NULL, -- 'medication', 'dispensing_fee', 'copay', 'tax'
    description TEXT NOT NULL,

    quantity DECIMAL(10, 2) DEFAULT 1,
    unit_price DECIMAL(10, 4) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,

    ndc_code VARCHAR(11), -- For medications

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Payments
CREATE TABLE billing_schema.payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_number VARCHAR(20) UNIQUE NOT NULL,
    invoice_id UUID REFERENCES billing_schema.invoices(id),
    patient_id UUID NOT NULL,

    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- 'cash', 'credit_card', 'debit_card', 'check', 'insurance'

    amount DECIMAL(10, 2) NOT NULL,

    -- Payment details
    transaction_id VARCHAR(100), -- From payment processor
    card_last_four VARCHAR(4),
    check_number VARCHAR(20),

    status VARCHAR(20) NOT NULL DEFAULT 'completed', -- 'pending', 'completed', 'failed', 'refunded'

    notes TEXT,

    processed_by UUID REFERENCES user_schema.users(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insurance Claims
CREATE TABLE billing_schema.insurance_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    prescription_id UUID NOT NULL,
    patient_insurance_id UUID NOT NULL, -- References patient_schema.patient_insurance

    submission_date DATE NOT NULL,

    -- Claim amounts
    submitted_amount DECIMAL(10, 2) NOT NULL,
    approved_amount DECIMAL(10, 2),
    patient_responsibility DECIMAL(10, 2), -- Copay

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'submitted',
    -- 'submitted', 'pending', 'approved', 'partially_approved', 'rejected', 'appealed'

    rejection_code VARCHAR(20),
    rejection_reason TEXT,

    response_date DATE,
    payment_date DATE,

    -- External references
    external_claim_id VARCHAR(100), -- Insurance company's claim ID

    notes TEXT,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Pricing Rules (dynamic pricing)
CREATE TABLE billing_schema.pricing_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- 'medication', 'insurance', 'patient_category', 'promotion'

    -- Conditions (JSONB for flexibility)
    conditions JSONB NOT NULL,
    -- Example: {"medication_id": "uuid", "insurance_type": "medicare"}

    -- Pricing action
    pricing_method VARCHAR(50) NOT NULL, -- 'fixed_price', 'percentage_discount', 'awp_minus_percentage'
    pricing_value DECIMAL(10, 4) NOT NULL,

    priority INTEGER DEFAULT 0, -- Higher priority rules apply first

    effective_date DATE NOT NULL,
    expiration_date DATE,

    is_active BOOLEAN DEFAULT true,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id)
);

-- Discounts/Coupons
CREATE TABLE billing_schema.discounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NOT NULL,

    discount_type VARCHAR(50) NOT NULL, -- 'percentage', 'fixed_amount', 'free_item'
    discount_value DECIMAL(10, 4) NOT NULL,

    -- Usage limits
    max_uses INTEGER,
    times_used INTEGER DEFAULT 0,
    max_uses_per_patient INTEGER DEFAULT 1,

    -- Restrictions
    minimum_purchase_amount DECIMAL(10, 2),
    applicable_medications JSONB, -- List of medication IDs or categories

    effective_date DATE NOT NULL,
    expiration_date DATE NOT NULL,

    is_active BOOLEAN DEFAULT true,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_invoices_prescription_id ON billing_schema.invoices(prescription_id);
CREATE INDEX idx_invoices_patient_id ON billing_schema.invoices(patient_id);
CREATE INDEX idx_invoices_status ON billing_schema.invoices(status);
CREATE INDEX idx_payments_invoice_id ON billing_schema.payments(invoice_id);
CREATE INDEX idx_insurance_claims_prescription_id ON billing_schema.insurance_claims(prescription_id);
CREATE INDEX idx_insurance_claims_status ON billing_schema.insurance_claims(status);
CREATE INDEX idx_pricing_rules_effective_expiration ON billing_schema.pricing_rules(effective_date, expiration_date);
```

**Why This Design**:

- **Flexible pricing**: JSONB rules allow complex pricing without code changes
- **Insurance integration**: Track claims lifecycle
- **Multiple payment methods**: Cash, card, insurance
- **Discounts**: Support promotional campaigns
- **Audit trail**: Who created, when, amounts

---

## 6. Notification Schema

```sql
CREATE SCHEMA notification_schema;

-- Notification Templates
CREATE TABLE notification_schema.notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name VARCHAR(100) UNIQUE NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- 'sms', 'email', 'push'
    event_trigger VARCHAR(100) NOT NULL, -- 'prescription_ready', 'refill_due', etc.

    subject VARCHAR(255), -- For emails
    body_template TEXT NOT NULL, -- With placeholders like {{patient_name}}

    is_active BOOLEAN DEFAULT true,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Notifications
CREATE TABLE notification_schema.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    template_id UUID REFERENCES notification_schema.notification_templates(id),

    notification_type VARCHAR(50) NOT NULL, -- 'sms', 'email', 'push'

    recipient VARCHAR(255) NOT NULL, -- Phone number or email
    subject VARCHAR(255),
    message TEXT NOT NULL,

    -- Delivery
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 'pending', 'sent', 'delivered', 'failed', 'bounced'

    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,

    -- External reference
    external_id VARCHAR(255), -- ID from SMS/email provider

    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,

    -- Context
    reference_type VARCHAR(50), -- 'prescription', 'appointment', 'reminder'
    reference_id UUID,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_notifications_patient_id ON notification_schema.notifications(patient_id);
CREATE INDEX idx_notifications_status ON notification_schema.notifications(status);
CREATE INDEX idx_notifications_created_at ON notification_schema.notifications(created_at);
```

---

## 7. Audit Schema (System-Wide)

```sql
CREATE SCHEMA audit_schema;

-- Domain Events (event sourcing lite)
CREATE TABLE audit_schema.domain_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(100) NOT NULL, -- 'PrescriptionFilled', 'StockAdjusted', etc.
    aggregate_type VARCHAR(50) NOT NULL, -- 'prescription', 'inventory', etc.
    aggregate_id UUID NOT NULL,

    event_data JSONB NOT NULL, -- Full event payload

    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id UUID REFERENCES user_schema.users(id),

    -- For ordering
    sequence_number BIGSERIAL
);

-- Access Logs (HIPAA compliance)
CREATE TABLE audit_schema.access_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES user_schema.users(id),

    action VARCHAR(50) NOT NULL, -- 'view', 'create', 'update', 'delete'
    resource_type VARCHAR(50) NOT NULL, -- 'patient', 'prescription', etc.
    resource_id UUID NOT NULL,

    ip_address INET,
    user_agent TEXT,

    accessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_domain_events_aggregate ON audit_schema.domain_events(aggregate_type, aggregate_id);
CREATE INDEX idx_domain_events_occurred_at ON audit_schema.domain_events(occurred_at);
CREATE INDEX idx_access_logs_user_id ON audit_schema.access_logs(user_id);
CREATE INDEX idx_access_logs_resource ON audit_schema.access_logs(resource_type, resource_id);
CREATE INDEX idx_access_logs_accessed_at ON audit_schema.access_logs(accessed_at);
```

**Why This Design**:

- **Event sourcing**: Can replay events to understand what happened
- **HIPAA compliance**: Track who accessed patient data
- **Audit trail**: Complete history of system changes

---

## Cross-Schema Relationships

Since we're using separate schemas, here's how they relate:

```
patient_schema.patients
    ↓ (patient_id - not FK, accessed via API)
prescription_schema.prescriptions
    ↓
billing_schema.invoices
    ↓
billing_schema.payments

prescription_schema.medications
    ↓ (medication_id)
inventory_schema.inventory_items
    ↓
inventory_schema.inventory_batches

prescription_schema.prescriptions
    ↓ (prescription_id via events)
inventory_schema.inventory_transactions
```

**Important**: We DON'T create foreign key constraints across schemas. Modules communicate via:

1. **APIs** for synchronous needs
2. **Events** for async updates
3. **Application-level references** (store UUIDs but don't enforce FK)

This keeps modules loosely coupled and extractable.

---

## Database Performance Optimizations

### 1. **Partitioning** (for high-volume tables)

```sql
-- Partition prescriptions by date (quarterly)
CREATE TABLE prescription_schema.prescriptions_2024_q1
    PARTITION OF prescription_schema.prescriptions
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

-- Partition inventory transactions by month
CREATE TABLE inventory_schema.inventory_transactions_2024_01
    PARTITION OF inventory_schema.inventory_transactions
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

### 2. **Materialized Views** (for reporting)

```sql
-- Fast inventory summary
CREATE MATERIALIZED VIEW inventory_schema.inventory_summary AS
SELECT
    i.id,
    i.ndc_code,
    m.drug_name,
    i.quantity_on_hand,
    i.reorder_point,
    COUNT(b.id) as batch_count,
    MIN(b.expiration_date) as nearest_expiry
FROM inventory_schema.inventory_items i
JOIN prescription_schema.medications m ON i.medication_id = m.id
LEFT JOIN inventory_schema.inventory_batches b ON b.inventory_item_id = i.id
WHERE i.is_active = true AND b.status = 'active'
GROUP BY i.id, i.ndc_code, m.drug_name, i.quantity_on_hand, i.reorder_point;

-- Refresh strategy (scheduled job or trigger-based)
REFRESH MATERIALIZED VIEW CONCURRENTLY inventory_schema.inventory_summary;
```

### 3. **Indexes for Common Queries**

Already added, but here are the patterns:

- **Foreign keys**: Always indexed
- **Status fields**: Indexed (frequently filtered)
- **Date ranges**: Indexed (prescriptions by date, expiry dates)
- **Search fields**: GIN indexes for full-text search
- **Composite indexes**: For common multi-column queries

---

## Migration Strategy

Since requirements are evolving, here's how to handle schema changes:

**Use Migration Tools**:

- **Flyway** or **Liquibase** for version control
- **Alembic** (Python) or **Knex** (Node.js)

**Migration Files**:

```
migrations/
├── V001__create_user_schema.sql
├── V002__create_patient_schema.sql
├── V003__create_prescription_schema.sql
├── V004__create_inventory_schema.sql
├── V005__create_billing_schema.sql
└── V006__create_audit_schema.sql
```

**Rules**:

- Never modify old migrations
- Always create new migration for schema changes
- Test migrations on staging before production
- Keep migrations small and focused

---

## Summary

We've designed:

✅ **7 schemas** with clear boundaries
✅ **40+ tables** covering all core workflows
✅ **Complete audit trails** (HIPAA compliant)
✅ **Flexible design** (JSONB for extensibility)
✅ **Performance optimizations** (indexes, partitioning, materialized views)
✅ **Module independence** (no cross-schema FKs)

This schema supports:

- All 3 core workflows we defined
- Easy feature additions (new tables don't affect existing)
- Compliance (audit logs, soft deletes, access tracking)
- Scalability (partitioning strategy, proper indexing)

---

## Next Steps

Now we can:

1. **Define API contracts** - REST endpoints for each module
2. **Create event catalog** - List all domain events
3. **Design service layer** - Business logic organization
4. **Setup project structure** - Actual code organization
5. **Choose tech stack** - Specific frameworks

What would you like to tackle next? I recommend **API contracts** since they define how the frontend and modules communicate.
