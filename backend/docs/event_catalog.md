## Event Design Principles

Before we list events, here are our principles:

1. **Past tense naming** - Events represent things that happened: `PrescriptionFilled` not `FillPrescription`
2. **Immutable** - Once published, events never change
3. **Self-contained** - Events contain all data needed by consumers
4. **Domain language** - Use business terminology
5. **Versioned** - Include version for evolution (`v1`, `v2`)
6. **Metadata** - Always include: eventId, timestamp, userId, aggregateId
7. **Granular** - Prefer specific events over generic ones

---

## Event Structure Template

```typescript
interface DomainEvent {
  // Event metadata
  eventId: string;           // UUID
  eventType: string;         // "PrescriptionFilled"
  eventVersion: string;      // "v1"
  
  // Aggregate information
  aggregateType: string;     // "prescription", "inventory", etc.
  aggregateId: string;       // ID of the entity
  
  // Causation (what caused this event)
  causationId?: string;      // ID of command/request that caused this
  correlationId?: string;    // For tracing related events
  
  // Timing
  occurredAt: string;        // ISO 8601 timestamp
  
  // Context
  userId: string;            // Who triggered this
  
  // Event-specific payload
  data: any;                 // Event-specific data
}
```

---

## Event Catalog by Module

### 1. Prescription Module Events

#### **PrescriptionReceived** (v1)
Triggered when a prescription is entered into the system

```typescript
{
  eventType: "PrescriptionReceived",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T10:00:00Z",
  userId: "user-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    prescriber: {
      name: "Dr. John Smith",
      npi: "1234567890",
      dea: "AS1234563"
    },
    prescriptionDate: "2025-12-30",
    source: "paper", // "paper", "electronic", "phone", "fax"
    isStat: false,
    itemCount: 2,
    items: [
      {
        prescriptionItemId: "item-uuid",
        medicationId: "med-uuid",
        medicationName: "Lisinopril 10mg",
        quantity: 30,
        daysSupply: 30,
        refillsAuthorized: 5
      }
    ]
  }
}
```

**Consumers**:
- Notification Service → Send "prescription received" notification
- Reporting Service → Update dashboard metrics
- Audit Service → Log access

---

#### **PrescriptionValidated** (v1)
Triggered after prescription passes all validations

```typescript
{
  eventType: "PrescriptionValidated",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T10:15:00Z",
  userId: "user-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    validations: [
      {
        type: "prescriber_license",
        status: "passed",
        message: "Prescriber license valid"
      },
      {
        type: "drug_interaction",
        status: "warning",
        severity: "minor",
        details: "Minor interaction with Aspirin"
      },
      {
        type: "allergy_check",
        status: "passed"
      },
      {
        type: "dea_verification",
        status: "passed"
      }
    ],
    hasWarnings: true,
    hasCriticalIssues: false
  }
}
```

**Consumers**:
- Billing Service → Start insurance verification
- Inventory Service → Reserve stock (soft reservation)
- Notification Service → Notify patient (if configured)

---

#### **PrescriptionValidationFailed** (v1)
Triggered when validation fails

```typescript
{
  eventType: "PrescriptionValidationFailed",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T10:15:00Z",
  userId: "user-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    failedValidations: [
      {
        type: "prescriber_license",
        status: "failed",
        message: "Prescriber license expired",
        severity: "critical"
      }
    ],
    requiresReview: true
  }
}
```

**Consumers**:
- Notification Service → Alert pharmacist manager
- Reporting Service → Track validation failure metrics

---

#### **PrescriptionFilled** (v1)
Triggered when prescription is filled/dispensed

```typescript
{
  eventType: "PrescriptionFilled",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "pharmacist-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    items: [
      {
        prescriptionItemId: "item-uuid",
        dispensedMedicationId: "med-uuid",
        dispensedNdc: "00071015423",
        dispensedQuantity: 30,
        batchId: "batch-uuid",
        batchNumber: "LOT12345"
      }
    ],
    counselingCompleted: true,
    dispensedBy: "pharmacist-uuid",
    dispensedAt: "2025-12-30T10:45:00Z"
  }
}
```

**Consumers**:
- **Inventory Service** → Deduct stock (critical)
- **Billing Service** → Generate invoice (critical)
- **Patient Service** → Update medication history
- **Notification Service** → Send "ready for pickup" SMS
- **Reporting Service** → Update sales metrics
- **Audit Service** → Log dispensing event

---

#### **PrescriptionRejected** (v1)
Triggered when prescription is rejected

```typescript
{
  eventType: "PrescriptionRejected",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T10:30:00Z",
  userId: "pharmacist-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    rejectionReason: "Invalid prescriber DEA number",
    rejectionCategory: "prescriber_verification_failed",
    notes: "Contacted prescriber office, unable to verify",
    notifyPrescriber: true,
    notifyPatient: true
  }
}
```

**Consumers**:
- Inventory Service → Release reserved stock
- Notification Service → Notify patient and prescriber
- Reporting Service → Track rejection metrics

---

#### **RefillRequested** (v1)
Triggered when refill is requested

```typescript
{
  eventType: "RefillRequested",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T11:00:00Z",
  userId: "user-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    prescriptionItemId: "item-uuid",
    refillNumber: 1,
    refillsRemaining: 4,
    requestedBy: "patient", // "patient", "pharmacist", "automated"
    canFillNow: true,
    nextAllowedFillDate: "2026-01-29",
    requiresPrescriberApproval: false
  }
}
```

**Consumers**:
- Prescription Service → Process refill
- Notification Service → Notify pharmacist
- Inventory Service → Check stock availability

---

#### **PrescriptionCancelled** (v1)
Triggered when prescription is cancelled

```typescript
{
  eventType: "PrescriptionCancelled",
  eventVersion: "v1",
  aggregateType: "prescription",
  aggregateId: "prescription-uuid",
  occurredAt: "2025-12-30T11:30:00Z",
  userId: "user-uuid",
  data: {
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    cancellationReason: "Patient request",
    cancelledBy: "pharmacist-uuid"
  }
}
```

**Consumers**:
- Inventory Service → Release reserved stock
- Billing Service → Cancel pending charges
- Notification Service → Notify patient

---

### 2. Inventory Module Events

#### **StockLevelChanged** (v1)
Triggered whenever stock quantity changes

```typescript
{
  eventType: "StockLevelChanged",
  eventVersion: "v1",
  aggregateType: "inventory_item",
  aggregateId: "inventory-item-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "user-uuid",
  data: {
    inventoryItemId: "inventory-item-uuid",
    medicationId: "med-uuid",
    ndcCode: "00071015423",
    medicationName: "Lisinopril 10mg",
    quantityBefore: 500,
    quantityAfter: 470,
    quantityChange: -30,
    changeType: "dispensed", // "received", "dispensed", "adjusted", "expired"
    batchId: "batch-uuid",
    referenceType: "prescription",
    referenceId: "prescription-uuid",
    reason: "Prescription RX789012 filled"
  }
}
```

**Consumers**:
- Reporting Service → Update inventory reports
- Inventory Service (self) → Check if below reorder point

---

#### **StockLevelLow** (v1)
Triggered when stock falls below reorder point

```typescript
{
  eventType: "StockLevelLow",
  eventVersion: "v1",
  aggregateType: "inventory_item",
  aggregateId: "inventory-item-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "system",
  data: {
    inventoryItemId: "inventory-item-uuid",
    medicationId: "med-uuid",
    ndcCode: "00071015423",
    medicationName: "Lisinopril 10mg",
    currentQuantity: 180,
    reorderPoint: 200,
    reorderQuantity: 1000,
    recommendedAction: "create_purchase_order",
    urgency: "normal", // "normal", "high", "critical"
    estimatedStockoutDate: "2026-01-05"
  }
}
```

**Consumers**:
- Notification Service → Alert inventory manager
- Inventory Service → Auto-create PO (if configured)
- Reporting Service → Update low stock dashboard

---

#### **StockReceived** (v1)
Triggered when stock is received from supplier

```typescript
{
  eventType: "StockReceived",
  eventVersion: "v1",
  aggregateType: "inventory_item",
  aggregateId: "inventory-item-uuid",
  occurredAt: "2025-12-30T09:00:00Z",
  userId: "user-uuid",
  data: {
    inventoryItemId: "inventory-item-uuid",
    medicationId: "med-uuid",
    ndcCode: "00071015423",
    medicationName: "Lisinopril 10mg",
    purchaseOrderId: "po-uuid",
    poNumber: "PO123456",
    supplierId: "supplier-uuid",
    supplierName: "McKesson Corporation",
    quantityReceived: 1000,
    batch: {
      batchId: "batch-uuid",
      batchNumber: "LOT99999",
      expirationDate: "2027-12-30",
      unitCost: 0.12
    },
    newTotalQuantity: 1180,
    receivedBy: "user-uuid"
  }
}
```

**Consumers**:
- Billing Service → Record cost
- Reporting Service → Update inventory value
- Notification Service → Confirm receipt to supplier

---

#### **ItemExpiring** (v1)
Triggered when items are nearing expiration

```typescript
{
  eventType: "ItemExpiring",
  eventVersion: "v1",
  aggregateType: "inventory_batch",
  aggregateId: "batch-uuid",
  occurredAt: "2025-12-30T00:00:00Z",
  userId: "system",
  data: {
    batchId: "batch-uuid",
    inventoryItemId: "inventory-item-uuid",
    medicationId: "med-uuid",
    medicationName: "Lisinopril 10mg",
    batchNumber: "LOT12345",
    expirationDate: "2026-03-15",
    daysUntilExpiry: 75,
    quantityRemaining: 100,
    estimatedValue: 15.00,
    recommendedAction: "prioritize_dispensing",
    canReturnToSupplier: false
  }
}
```

**Consumers**:
- Notification Service → Alert pharmacy manager
- Inventory Service → Flag for priority use
- Reporting Service → Track expiry costs

---

#### **StockAdjusted** (v1)
Triggered when manual stock adjustment is made

```typescript
{
  eventType: "StockAdjusted",
  eventVersion: "v1",
  aggregateType: "inventory_item",
  aggregateId: "inventory-item-uuid",
  occurredAt: "2025-12-30T14:00:00Z",
  userId: "user-uuid",
  data: {
    adjustmentId: "adjustment-uuid",
    inventoryItemId: "inventory-item-uuid",
    medicationName: "Lisinopril 10mg",
    adjustmentType: "physical_count",
    quantityExpected: 500,
    quantityActual: 485,
    quantityDifference: -15,
    reason: "Physical inventory count - shrinkage detected",
    batchId: "batch-uuid",
    requiresApproval: true,
    approvedBy: "manager-uuid",
    approvedAt: "2025-12-30T14:15:00Z"
  }
}
```

**Consumers**:
- Reporting Service → Track shrinkage
- Audit Service → Log adjustment for compliance
- Billing Service → Adjust inventory value

---

#### **PurchaseOrderCreated** (v1)
Triggered when PO is created

```typescript
{
  eventType: "PurchaseOrderCreated",
  eventVersion: "v1",
  aggregateType: "purchase_order",
  aggregateId: "po-uuid",
  occurredAt: "2025-12-30T10:00:00Z",
  userId: "user-uuid",
  data: {
    poId: "po-uuid",
    poNumber: "PO123457",
    supplierId: "supplier-uuid",
    supplierName: "McKesson Corporation",
    orderDate: "2025-12-30",
    expectedDeliveryDate: "2026-01-10",
    totalAmount: 5000.00,
    itemCount: 15,
    items: [
      {
        inventoryItemId: "inventory-item-uuid",
        medicationName: "Lisinopril 10mg",
        ndcCode: "00071015423",
        quantityOrdered: 1000,
        unitPrice: 0.12
      }
    ],
    createdBy: "user-uuid"
  }
}
```

**Consumers**:
- Notification Service → Send PO to supplier
- Reporting Service → Track purchasing metrics

---

### 3. Patient Module Events

#### **PatientRegistered** (v1)
Triggered when new patient is registered

```typescript
{
  eventType: "PatientRegistered",
  eventVersion: "v1",
  aggregateType: "patient",
  aggregateId: "patient-uuid",
  occurredAt: "2025-12-30T10:00:00Z",
  userId: "user-uuid",
  data: {
    patientId: "patient-uuid",
    patientNumber: "P123456",
    firstName: "Alice",
    lastName: "Johnson",
    dateOfBirth: "1985-05-15",
    phone: "+1234567890",
    email: "alice@email.com",
    hasInsurance: true,
    hasAllergies: true,
    allergyCount: 1,
    registeredBy: "user-uuid"
  }
}
```

**Consumers**:
- Notification Service → Send welcome message
- Reporting Service → Update patient demographics
- Audit Service → Log patient creation (HIPAA)

---

#### **PatientUpdated** (v1)
Triggered when patient info is updated

```typescript
{
  eventType: "PatientUpdated",
  eventVersion: "v1",
  aggregateType: "patient",
  aggregateId: "patient-uuid",
  occurredAt: "2025-12-30T11:00:00Z",
  userId: "user-uuid",
  data: {
    patientId: "patient-uuid",
    patientNumber: "P123456",
    updatedFields: ["phone", "email", "address"],
    changes: {
      phone: {
        oldValue: "+1234567890",
        newValue: "+1234567899"
      },
      email: {
        oldValue: "alice@email.com",
        newValue: "alice.new@email.com"
      }
    },
    updatedBy: "user-uuid"
  }
}
```

**Consumers**:
- Audit Service → Log changes (HIPAA)
- Notification Service → Confirm changes to patient

---

#### **AllergyAdded** (v1)
Triggered when allergy is added to patient record

```typescript
{
  eventType: "AllergyAdded",
  eventVersion: "v1",
  aggregateType: "patient",
  aggregateId: "patient-uuid",
  occurredAt: "2025-12-30T11:30:00Z",
  userId: "user-uuid",
  data: {
    patientId: "patient-uuid",
    patientNumber: "P123456",
    allergyId: "allergy-uuid",
    allergenType: "medication",
    allergenName: "Sulfa drugs",
    reaction: "Hives",
    severity: "severe",
    addedBy: "user-uuid"
  }
}
```

**Consumers**:
- Prescription Service → Re-check active prescriptions
- Notification Service → Alert pharmacists
- Audit Service → Log allergy addition

---

#### **InsuranceUpdated** (v1)
Triggered when insurance info changes

```typescript
{
  eventType: "InsuranceUpdated",
  eventVersion: "v1",
  aggregateType: "patient",
  aggregateId: "patient-uuid",
  occurredAt: "2025-12-30T12:00:00Z",
  userId: "user-uuid",
  data: {
    patientId: "patient-uuid",
    patientNumber: "P123456",
    insuranceId: "insurance-uuid",
    changeType: "new_policy", // "new_policy", "updated", "removed"
    insuranceProvider: "Blue Cross",
    policyNumber: "BC123456789",
    effectiveDate: "2026-01-01",
    updatedBy: "user-uuid"
  }
}
```

**Consumers**:
- Billing Service → Update pricing calculations
- Notification Service → Confirm insurance update

---

### 4. Billing Module Events

#### **InvoiceGenerated** (v1)
Triggered when invoice is created

```typescript
{
  eventType: "InvoiceGenerated",
  eventVersion: "v1",
  aggregateType: "invoice",
  aggregateId: "invoice-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "system",
  data: {
    invoiceId: "invoice-uuid",
    invoiceNumber: "INV123456",
    prescriptionId: "prescription-uuid",
    prescriptionNumber: "RX789012",
    patientId: "patient-uuid",
    patientNumber: "P123456",
    invoiceDate: "2025-12-30",
    subtotal: 45.50,
    taxAmount: 0.00,
    discountAmount: 0.00,
    insurancePayment: 35.50,
    patientCopay: 10.00,
    totalAmount: 45.50,
    items: [
      {
        itemType: "medication",
        description: "Lisinopril 10mg - 30 tablets",
        quantity: 30,
        unitPrice: 1.50,
        totalPrice: 45.00
      }
    ]
  }
}
```

**Consumers**:
- Notification Service → Send invoice to patient (email)
- Reporting Service → Update revenue metrics
- Billing Service (self) → Track aging

---

#### **PaymentReceived** (v1)
Triggered when payment is processed

```typescript
{
  eventType: "PaymentReceived",
  eventVersion: "v1",
  aggregateType: "payment",
  aggregateId: "payment-uuid",
  occurredAt: "2025-12-30T10:50:00Z",
  userId: "user-uuid",
  data: {
    paymentId: "payment-uuid",
    paymentNumber: "PAY789012",
    invoiceId: "invoice-uuid",
    invoiceNumber: "INV123456",
    patientId: "patient-uuid",
    amount: 10.00,
    paymentMethod: "credit_card",
    cardLastFour: "1234",
    transactionId: "txn_abc123xyz",
    paymentDate: "2025-12-30",
    processedBy: "user-uuid",
    invoiceNewStatus: "paid",
    invoiceRemainingBalance: 0.00
  }
}
```

**Consumers**:
- Notification Service → Send receipt to patient
- Reporting Service → Update cash flow metrics
- Billing Service (self) → Update invoice status

---

#### **PaymentFailed** (v1)
Triggered when payment processing fails

```typescript
{
  eventType: "PaymentFailed",
  eventVersion: "v1",
  aggregateType: "payment",
  aggregateId: "payment-uuid",
  occurredAt: "2025-12-30T10:50:00Z",
  userId: "user-uuid",
  data: {
    paymentId: "payment-uuid",
    invoiceId: "invoice-uuid",
    patientId: "patient-uuid",
    amount: 10.00,
    paymentMethod: "credit_card",
    failureReason: "insufficient_funds",
    errorCode: "card_declined",
    errorMessage: "Card was declined",
    retryAttempt: 1,
    maxRetries: 3
  }
}
```

**Consumers**:
- Notification Service → Alert staff and patient
- Billing Service → Schedule retry or mark for follow-up

---

#### **InsuranceClaimSubmitted** (v1)
Triggered when insurance claim is sent

```typescript
{
  eventType: "InsuranceClaimSubmitted",
  eventVersion: "v1",
  aggregateType: "insurance_claim",
  aggregateId: "claim-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "system",
  data: {
    claimId: "claim-uuid",
    claimNumber: "CLM123456",
    prescriptionId: "prescription-uuid",
    patientInsuranceId: "insurance-uuid",
    insuranceProvider: "Blue Cross",
    submittedAmount: 45.50,
    submissionDate: "2025-12-30",
    externalClaimId: "INS789012"
  }
}
```

**Consumers**:
- Notification Service → Log submission
- Reporting Service → Track claim metrics

---

#### **InsuranceClaimApproved** (v1)
Triggered when insurance approves claim

```typescript
{
  eventType: "InsuranceClaimApproved",
  eventVersion: "v1",
  aggregateType: "insurance_claim",
  aggregateId: "claim-uuid",
  occurredAt: "2025-12-30T11:00:00Z",
  userId: "system",
  data: {
    claimId: "claim-uuid",
    claimNumber: "CLM123456",
    prescriptionId: "prescription-uuid",
    submittedAmount: 45.50,
    approvedAmount: 35.50,
    patientResponsibility: 10.00,
    responseDate: "2025-12-30",
    paymentDate: "2026-01-15",
    externalClaimId: "INS789012"
  }
}
```

**Consumers**:
- Billing Service → Update invoice with final amounts
- Notification Service → Notify patient of copay
- Reporting Service → Track approval rate

---

#### **InsuranceClaimRejected** (v1)
Triggered when insurance rejects claim

```typescript
{
  eventType: "InsuranceClaimRejected",
  eventVersion: "v1",
  aggregateType: "insurance_claim",
  aggregateId: "claim-uuid",
  occurredAt: "2025-12-30T11:00:00Z",
  userId: "system",
  data: {
    claimId: "claim-uuid",
    claimNumber: "CLM123456",
    prescriptionId: "prescription-uuid",
    submittedAmount: 45.50,
    rejectionCode: "70",
    rejectionReason: "Patient not covered",
    responseDate: "2025-12-30",
    canAppeal: true,
    requiresPatientPayment: true
  }
}
```

**Consumers**:
- Billing Service → Switch to cash payment
- Notification Service → Alert staff and patient
- Reporting Service → Track rejection reasons

---

### 5. Notification Module Events

#### **NotificationSent** (v1)
Triggered when notification is successfully sent

```typescript
{
  eventType: "NotificationSent",
  eventVersion: "v1",
  aggregateType: "notification",
  aggregateId: "notification-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "system",
  data: {
    notificationId: "notification-uuid",
    patientId: "patient-uuid",
    notificationType: "sms",
    recipient: "+1234567890",
    message: "Your prescription RX789012 is ready for pickup",
    sentAt: "2025-12-30T10:45:00Z",
    externalId: "sms-provider-id-123",
    referenceType: "prescription",
    referenceId: "prescription-uuid"
  }
}
```

**Consumers**:
- Reporting Service → Track notification metrics
- Audit Service → Log communication (HIPAA)

---

#### **NotificationFailed** (v1)
Triggered when notification fails to send

```typescript
{
  eventType: "NotificationFailed",
  eventVersion: "v1",
  aggregateType: "notification",
  aggregateId: "notification-uuid",
  occurredAt: "2025-12-30T10:45:00Z",
  userId: "system",
  data: {
    notificationId: "notification-uuid",
    patientId: "patient-uuid",
    notificationType: "sms",
    recipient: "+1234567890",
    message: "Your prescription RX789012 is ready for pickup",
    failureReason: "invalid_phone_number",
    errorMessage: "The phone number format is invalid",
    retryAttempt: 1,
    maxRetries: 3,
    willRetry: true,
    nextRetryAt: "2025-12-30T11:00:00Z"
  }
}
```

**Consumers**:
- Notification Service (self) → Schedule retry
- User Service → Flag patient contact info for update

---

### 6. User Module Events

#### **UserLoggedIn** (v1)
Triggered on successful login

```typescript
{
  eventType: "UserLoggedIn",
  eventVersion: "v1",
  aggregateType: "user",
  aggregateId: "user-uuid",
  occurredAt: "2025-12-30T09:00:00Z",
  userId: "user-uuid",
  data: {
    userId: "user-uuid",
    username: "john.pharmacist",
    role: "pharmacist",
    ipAddress: "192.168.1.100",
    userAgent: "Mozilla/5.0...",
    sessionId: "session-uuid"
  }
}
```

**Consumers**:
- Audit Service → Log login for security
- Reporting Service → Track user activity

---

#### **UserPasswordChanged** (v1)
Triggered when password is changed

```typescript
{
  eventType: "UserPasswordChanged",
  eventVersion: "v1",
  aggregateType: "user",
  aggregateId: "user-uuid",
  occurredAt: "2025-12-30T14:00:00Z",
  userId: "user-uuid",
  data: {
    userId: "user-uuid",
    username: "john.pharmacist",
    changedBy: "user-uuid", // Self or admin
    ipAddress: "192.168.1.100"
  }
}
```

**Consumers**:
- Notification Service → Send confirmation email
- Audit Service → Log security event
- User Service (self) → Revoke all existing sessions

---

### 7. System Events

#### **SystemHealthCheckFailed** (v1)
Triggered when health check fails

```typescript
{
  eventType: "SystemHealthCheckFailed",
  eventVersion: "v1",
  aggregateType: "system",
  aggregateId: "health-check-uuid",
  occurredAt: "2025-12-30T10:00:00Z",
  userId: "system",
  data: {
    component: "database", // "database", "redis", "external_api", "queue"
    healthCheckType: "connectivity",
    failureReason: "Connection timeout",
    severity: "critical",
    affectedServices: ["prescription", "inventory"],
    recoveryAction: "automatic_retry"
  }
}
```

**Consumers**:
- Notification Service → Alert operations team
- Monitoring Service → Trigger alerts

---

## Event Processing Patterns

### 1. **Synchronous Event Processing** (Same Transaction)
For critical operations that must complete together:

```typescript
// When filling prescription
async function fillPrescription(prescriptionId) {
  await db.transaction(async (trx) => {
    // Update prescription
    await updatePrescription(prescriptionId, { status: 'filled' }, trx);
    
    // Publish event within same transaction
    await publishEvent(new PrescriptionFilled(...), trx);
  });
  
  // After commit, event is guaranteed to be published
}
```

### 2. **Asynchronous Event Processing** (Eventually Consistent)
For non-critical operations:

```typescript
// Inventory service subscribes
eventBus.subscribe('PrescriptionFilled', async (event) => {
  try {
    await deductStock(event.data.items);
  } catch (error) {
    // Log error,retry logic
    await scheduleRetry(event);
  }
});
```

### 3. **Event Replay** (For Debugging/Recovery)
Ability to replay events:

```typescript
// Replay all inventory events to rebuild state
const events = await getEvents({
  aggregateType: 'inventory_item',
  aggregateId: 'inventory-item-uuid',
  fromTimestamp: '2025-01-01'
});

for (const event of events) {
  await replayEvent(event);
}
```

---

## Event Storage

Store all events in the audit schema:

```sql
CREATE TABLE audit_schema.domain_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_version VARCHAR(10) NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id UUID NOT NULL,
    causation_id UUID,
    correlation_id UUID,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID,
    event_data JSONB NOT NULL,
    metadata JSONB,
    
    -- For ordering and replay
    sequence_number BIGSERIAL,
    
    -- Indexes
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_domain_events_aggregate ON audit_schema.domain_events(aggregate_type, aggregate_id);
CREATE INDEX idx_domain_events_type ON audit_schema.domain_events(event_type);
CREATE INDEX idx_domain_events_occurred_at ON audit_schema.domain_events(occurred_at);
CREATE INDEX idx_domain_events_correlation ON audit_schema.domain_events(correlation_id);
```

---

## Event Versioning Strategy

When events evolve:

```typescript
// Version 1
interface PrescriptionFilledV1 {
  prescriptionId: string;
  items: Item[];
}

// Version 2 (added counseling info)
interface PrescriptionFilledV2 {
  prescriptionId: string;
  items: Item[];
  counselingCompleted: boolean;  // NEW
  counselingNotes?: string;      // NEW
}

// Upcasting v1 to v2
function upcast(event: PrescriptionFilledV1): PrescriptionFilledV2 {
  return {
    ...event,
    counselingCompleted: false,  // Default for old events
    counselingNotes: undefined
  };
}
```

---

## Event Summary Table

| Event | Trigger | Critical Consumers | Priority |
|-------|---------|-------------------|----------|
| **PrescriptionReceived** | Prescription entered | Audit, Notification | Medium |
| **PrescriptionValidated** | Validation passed | Billing, Inventory | High |
| **PrescriptionFilled** | Prescription dispensed | Inventory, Billing, Patient | **CRITICAL** |
| **PrescriptionRejected** | Prescription rejected | Inventory, Notification | High |
| **RefillRequested** | Refill requested | Prescription, Inventory | Medium |
| **StockLevelChanged** | Stock quantity changes | Inventory (self) | Medium |
| **StockLevelLow** | Below reorder point | Notification, Inventory | High |
| **StockReceived** | Stock delivered | Billing, Reporting | Medium |
| **ItemExpiring** | Nearing expiration | Notification, Inventory | Medium |
| **PatientRegistered** | New patient | Notification, Reporting | Low |
| **AllergyAdded** | Allergy added | Prescription, Notification | **CRITICAL** |
| **InvoiceGenerated** | Invoice created | Notification, Reporting | Medium |
| **PaymentReceived** | Payment processed | Notification, Reporting | High |
| **InsuranceClaimSubmitted** | Claim sent | Reporting | Low |
| **InsuranceClaimApproved** | Claim approved | Billing, Notification | High |
| **InsuranceClaimRejected** | Claim rejected | Billing, Notification | High |

---

## Event Bus Implementation Options

### Option 1: In-Memory (Start Here)
```typescript
// Simple pub/sub for monolith
class InMemoryEventBus {
  private subscribers = new Map<string, Function[]>();
  
  subscribe(eventType: string, handler: Function) {
    if (!this.subscribers.has(eventType)) {
      this.subscribers.set(eventType, []);
    }
    this.subscribers.get(eventType)!.push(handler);
  }
  
  async publish(event: DomainEvent) {
    // Store event
    await storeEvent(event);
    
    // Notify subscribers
    const handlers = this.subscribers.get(event.eventType) || [];
    await Promise.all(handlers.map(h => h(event)));
  }
}
```

### Option 2: RabbitMQ (When You Need Durability)
```typescript
// RabbitMQ for guaranteed delivery
const connection = await amqp.connect('amqp://localhost');
const channel = await connection.createChannel();

// Publish
await channel.assertExchange('pharmacy.events', 'topic', { durable: true });
await channel.publish(
  'pharmacy.events',
  'prescription.filled',
  Buffer.from(JSON.stringify(event)),
  { persistent: true }
);

// Subscribe
await channel.assertQueue('inventory.queue', { durable: true });
await channel.bindQueue('inventory.queue', 'pharmacy.events', 'prescription.*');
await channel.consume('inventory.queue', handleEvent);
```

---

## Summary

We've defined:

✅ **40+ domain events** across all modules
✅ **Event structure** with metadata, versioning
✅ **Processing patterns** (sync, async, replay)
✅ **Storage strategy** (audit table)
✅ **Versioning approach** (upcasting)
✅ **Implementation options** (in-memory → RabbitMQ)

These events enable:
- Loose coupling between modules
- Easy feature additions (subscribe to existing events)
- Complete audit trail
- Event sourcing capabilities
- Debugging and replay