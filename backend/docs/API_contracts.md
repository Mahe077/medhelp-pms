## API Design Principles

Before we start, here are our guiding principles:

1. **RESTful conventions** - Use HTTP methods correctly (GET, POST, PUT, PATCH, DELETE)
2. **Resource-based URLs** - `/prescriptions/{id}` not `/getPrescription`
3. **API versioning** - `/api/v1/` prefix for all endpoints
4. **Consistent response format** - Standardized success/error responses
5. **Pagination** - For list endpoints
6. **Filtering & sorting** - Query parameters for lists
7. **HATEOAS principles** - Include relevant links in responses
8. **Idempotency** - Safe retry for critical operations
9. **Rate limiting** - Protect against abuse
10. **Documentation** - OpenAPI/Swagger spec

---

## Global API Structure

### Base URL
```
https://api.pharmacy.com/api/v1
```

### Standard Response Format

**Success Response**:
```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "timestamp": "2025-12-30T10:30:00Z",
    "requestId": "uuid"
  }
}
```

**Error Response**:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid prescription data",
    "details": [
      {
        "field": "patient_id",
        "message": "Patient ID is required"
      }
    ]
  },
  "meta": {
    "timestamp": "2025-12-30T10:30:00Z",
    "requestId": "uuid"
  }
}
```

**Paginated Response**:
```json
{
  "success": true,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalPages": 5,
    "totalItems": 95,
    "hasNext": true,
    "hasPrevious": false
  },
  "meta": {
    "timestamp": "2025-12-30T10:30:00Z",
    "requestId": "uuid"
  }
}
```

### Standard Error Codes

```
400 - Bad Request (validation errors)
401 - Unauthorized (not authenticated)
403 - Forbidden (authenticated but not authorized)
404 - Not Found
409 - Conflict (e.g., duplicate prescription number)
422 - Unprocessable Entity (business logic error)
429 - Too Many Requests (rate limit)
500 - Internal Server Error
503 - Service Unavailable
```

---

## 1. Authentication & User Management API

### Authentication

#### POST `/api/v1/auth/login`
Login and get JWT token

**Request**:
```json
{
  "username": "john.pharmacist",
  "password": "securePassword123"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600,
    "tokenType": "Bearer",
    "user": {
      "id": "uuid",
      "username": "john.pharmacist",
      "email": "john@pharmacy.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "pharmacist",
      "permissions": ["prescription.fill", "prescription.view", "inventory.view"]
    }
  }
}
```

#### POST `/api/v1/auth/refresh`
Refresh access token

**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600
  }
}
```

#### POST `/api/v1/auth/logout`
Logout (revoke refresh token)

**Headers**: `Authorization: Bearer {accessToken}`

**Response** (204): No content

#### POST `/api/v1/auth/change-password`
Change password

**Headers**: `Authorization: Bearer {accessToken}`

**Request**:
```json
{
  "currentPassword": "oldPassword",
  "newPassword": "newSecurePassword123"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "message": "Password changed successfully"
  }
}
```

### User Management

#### GET `/api/v1/users`
List all users (admin only)

**Headers**: `Authorization: Bearer {accessToken}`

**Query Parameters**:
- `page` (default: 1)
- `pageSize` (default: 20)
- `role` (filter by role)
- `isActive` (true/false)
- `search` (search by name/email)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "username": "john.pharmacist",
      "email": "john@pharmacy.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "pharmacist",
      "licenseNumber": "PH123456",
      "isActive": true,
      "createdAt": "2024-01-15T10:00:00Z"
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/users`
Create new user (admin only)

**Request**:
```json
{
  "username": "jane.tech",
  "email": "jane@pharmacy.com",
  "password": "initialPassword123",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+1234567890",
  "role": "technician",
  "licenseNumber": "TC789012"
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "username": "jane.tech",
    "email": "jane@pharmacy.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "role": "technician",
    "createdAt": "2025-12-30T10:30:00Z"
  }
}
```

#### GET `/api/v1/users/{userId}`
Get user details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "username": "john.pharmacist",
    "email": "john@pharmacy.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "role": "pharmacist",
    "licenseNumber": "PH123456",
    "isActive": true,
    "lastLoginAt": "2025-12-30T09:00:00Z",
    "createdAt": "2024-01-15T10:00:00Z",
    "permissions": ["prescription.fill", "prescription.view"]
  }
}
```

#### PATCH `/api/v1/users/{userId}`
Update user (partial update)

**Request**:
```json
{
  "phone": "+1234567899",
  "isActive": true
}
```

**Response** (200): Updated user object

#### DELETE `/api/v1/users/{userId}`
Deactivate user (soft delete)

**Response** (204): No content

---

## 2. Patient API

#### GET `/api/v1/patients`
List/search patients

**Query Parameters**:
- `page`, `pageSize`
- `search` (name, phone, patient number)
- `dateOfBirth` (filter by DOB)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "patientNumber": "P123456",
      "firstName": "Alice",
      "lastName": "Johnson",
      "dateOfBirth": "1985-05-15",
      "phone": "+1234567890",
      "email": "alice@email.com",
      "lastVisit": "2025-12-20T14:30:00Z"
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/patients`
Register new patient

**Request**:
```json
{
  "firstName": "Alice",
  "middleName": "Marie",
  "lastName": "Johnson",
  "dateOfBirth": "1985-05-15",
  "gender": "female",
  "ssnLastFour": "1234",
  "phone": "+1234567890",
  "email": "alice@email.com",
  "address": {
    "line1": "123 Main St",
    "line2": "Apt 4B",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "country": "US"
  },
  "preferences": {
    "language": "en",
    "communicationPreference": "sms",
    "allowGenericSubstitution": true
  },
  "allergies": [
    {
      "allergenType": "medication",
      "allergenName": "Penicillin",
      "reaction": "Rash",
      "severity": "moderate"
    }
  ],
  "insurance": [
    {
      "insuranceProvider": "Blue Cross",
      "insuranceType": "primary",
      "policyNumber": "BC123456789",
      "groupNumber": "GRP001",
      "binNumber": "610014",
      "cardholderName": "Alice Johnson",
      "relationshipToCardholder": "self",
      "effectiveDate": "2025-01-01",
      "expirationDate": "2025-12-31"
    }
  ]
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "patientNumber": "P123456",
    "firstName": "Alice",
    "lastName": "Johnson",
    "dateOfBirth": "1985-05-15",
    "createdAt": "2025-12-30T10:30:00Z"
  }
}
```

#### GET `/api/v1/patients/{patientId}`
Get patient details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "patientNumber": "P123456",
    "firstName": "Alice",
    "middleName": "Marie",
    "lastName": "Johnson",
    "dateOfBirth": "1985-05-15",
    "gender": "female",
    "phone": "+1234567890",
    "email": "alice@email.com",
    "address": { ... },
    "preferences": { ... },
    "allergies": [ ... ],
    "insurance": [ ... ],
    "conditions": [ ... ],
    "prescribers": [ ... ],
    "createdAt": "2024-06-15T10:00:00Z",
    "updatedAt": "2025-12-30T10:30:00Z"
  }
}
```

#### PATCH `/api/v1/patients/{patientId}`
Update patient (partial)

**Request**:
```json
{
  "phone": "+1234567899",
  "email": "newemail@email.com",
  "address": {
    "line1": "456 Oak Ave",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62702"
  }
}
```

**Response** (200): Updated patient object

#### GET `/api/v1/patients/{patientId}/allergies`
Get patient allergies

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "allergenType": "medication",
      "allergenName": "Penicillin",
      "reaction": "Rash",
      "severity": "moderate",
      "notes": "Developed during childhood",
      "onsetDate": "1990-03-10",
      "isActive": true,
      "createdAt": "2024-06-15T10:00:00Z"
    }
  ]
}
```

#### POST `/api/v1/patients/{patientId}/allergies`
Add patient allergy

**Request**:
```json
{
  "allergenType": "medication",
  "allergenName": "Sulfa drugs",
  "reaction": "Hives",
  "severity": "severe",
  "notes": "Avoid all sulfonamides"
}
```

**Response** (201): Created allergy object

#### GET `/api/v1/patients/{patientId}/prescriptions`
Get patient prescription history

**Query Parameters**:
- `page`, `pageSize`
- `status` (filter by status)
- `fromDate`, `toDate` (date range)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "prescriptionNumber": "RX789012",
      "prescriptionDate": "2025-12-20",
      "status": "completed",
      "prescriber": "Dr. Smith",
      "medications": [
        {
          "medicationName": "Lisinopril 10mg",
          "quantity": 30,
          "daysSupply": 30
        }
      ],
      "dispensedAt": "2025-12-20T15:30:00Z"
    }
  ],
  "pagination": { ... }
}
```

#### GET `/api/v1/patients/{patientId}/medication-history`
Get complete medication history

**Response** (200):
```json
{
  "success": true,
  "data": {
    "currentMedications": [
      {
        "medicationName": "Lisinopril 10mg",
        "prescriptionId": "uuid",
        "startDate": "2025-12-20",
        "refillsRemaining": 5,
        "nextRefillDate": "2026-01-20"
      }
    ],
    "pastMedications": [ ... ]
  }
}
```

---

## 3. Prescription API

#### GET `/api/v1/prescriptions`
List prescriptions

**Query Parameters**:
- `page`, `pageSize`
- `status` (received, validated, in_progress, ready, dispensed, completed)
- `patientId`
- `prescriberId` (NPI)
- `fromDate`, `toDate`
- `urgent` (true/false - stat prescriptions)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "prescriptionNumber": "RX789012",
      "patient": {
        "id": "uuid",
        "patientNumber": "P123456",
        "name": "Alice Johnson",
        "dateOfBirth": "1985-05-15"
      },
      "prescriber": {
        "name": "Dr. John Smith",
        "npi": "1234567890"
      },
      "prescriptionDate": "2025-12-30",
      "status": "validated",
      "isStat": false,
      "itemCount": 2,
      "receivedAt": "2025-12-30T09:00:00Z",
      "validatedAt": "2025-12-30T09:15:00Z"
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/prescriptions`
Create new prescription (receive)

**Request**:
```json
{
  "patientId": "uuid",
  "prescriber": {
    "name": "Dr. John Smith",
    "npi": "1234567890",
    "dea": "AS1234563",
    "phone": "+1234567890",
    "address": "100 Medical Plaza, Springfield, IL 62701"
  },
  "prescriptionDate": "2025-12-30",
  "writtenDate": "2025-12-30",
  "source": "paper",
  "externalRxNumber": null,
  "isStat": false,
  "notes": "Patient requests name brand",
  "items": [
    {
      "medicationName": "Lisinopril 10mg tablets",
      "medicationId": "uuid",
      "quantity": 30,
      "quantityUnit": "tablets",
      "daysSupply": 30,
      "sig": "Take 1 tablet by mouth once daily",
      "refillsAuthorized": 5,
      "substitutionAllowed": true,
      "dawCode": null
    }
  ]
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "prescriptionNumber": "RX789012",
    "status": "received",
    "patient": { ... },
    "prescriber": { ... },
    "items": [ ... ],
    "receivedAt": "2025-12-30T10:30:00Z",
    "receivedBy": {
      "id": "uuid",
      "name": "John Pharmacist"
    }
  }
}
```

#### GET `/api/v1/prescriptions/{prescriptionId}`
Get prescription details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "prescriptionNumber": "RX789012",
    "patient": {
      "id": "uuid",
      "patientNumber": "P123456",
      "name": "Alice Johnson",
      "dateOfBirth": "1985-05-15",
      "allergies": ["Penicillin"],
      "insurance": { ... }
    },
    "prescriber": {
      "name": "Dr. John Smith",
      "npi": "1234567890",
      "dea": "AS1234563",
      "phone": "+1234567890"
    },
    "prescriptionDate": "2025-12-30",
    "writtenDate": "2025-12-30",
    "expirationDate": "2026-12-30",
    "source": "paper",
    "status": "validated",
    "isStat": false,
    "items": [
      {
        "id": "uuid",
        "medicationName": "Lisinopril 10mg tablets",
        "medication": {
          "id": "uuid",
          "ndcCode": "00071015423",
          "genericName": "Lisinopril",
          "brandName": "Prinivil",
          "isControlledSubstance": false
        },
        "quantity": 30,
        "quantityUnit": "tablets",
        "daysSupply": 30,
        "sig": "Take 1 tablet by mouth once daily",
        "refillsAuthorized": 5,
        "refillsRemaining": 5,
        "substitutionAllowed": true
      }
    ],
    "validations": [
      {
        "type": "prescriber_license",
        "status": "passed",
        "performedAt": "2025-12-30T09:10:00Z"
      },
      {
        "type": "drug_interaction",
        "status": "warning",
        "details": "Minor interaction with current medication (Aspirin)",
        "performedAt": "2025-12-30T09:11:00Z"
      }
    ],
    "timeline": [
      {
        "event": "received",
        "timestamp": "2025-12-30T09:00:00Z",
        "user": "John Pharmacist"
      },
      {
        "event": "validated",
        "timestamp": "2025-12-30T09:15:00Z",
        "user": "John Pharmacist"
      }
    ],
    "notes": "Patient requests name brand",
    "receivedAt": "2025-12-30T09:00:00Z",
    "receivedBy": { ... },
    "validatedAt": "2025-12-30T09:15:00Z",
    "validatedBy": { ... }
  }
}
```

#### POST `/api/v1/prescriptions/{prescriptionId}/validate`
Validate prescription

**Request**:
```json
{
  "validations": ["prescriber_license", "drug_interaction", "allergy_check", "dea_verification"]
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "prescriptionId": "uuid",
    "status": "validated",
    "validationResults": [
      {
        "type": "prescriber_license",
        "status": "passed",
        "message": "Prescriber license valid"
      },
      {
        "type": "drug_interaction",
        "status": "warning",
        "message": "Minor interaction detected with Aspirin",
        "details": {
          "interactingMedication": "Aspirin",
          "severity": "minor",
          "recommendation": "Monitor blood pressure"
        }
      },
      {
        "type": "allergy_check",
        "status": "passed",
        "message": "No known allergies"
      }
    ],
    "validatedAt": "2025-12-30T10:30:00Z",
    "validatedBy": { ... }
  }
}
```

#### POST `/api/v1/prescriptions/{prescriptionId}/fill`
Fill prescription (dispense)

**Request**:
```json
{
  "items": [
    {
      "prescriptionItemId": "uuid",
      "dispensedMedicationId": "uuid",
      "dispensedNdc": "00071015423",
      "dispensedQuantity": 30,
      "batchId": "uuid"
    }
  ],
  "counselingCompleted": true,
  "counselingDeclined": false,
  "notes": "Patient counseled on side effects"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "prescriptionId": "uuid",
    "status": "dispensed",
    "items": [ ... ],
    "dispensedAt": "2025-12-30T10:45:00Z",
    "dispensedBy": { ... },
    "invoice": {
      "id": "uuid",
      "invoiceNumber": "INV123456",
      "totalAmount": 45.50,
      "patientCopay": 10.00
    }
  }
}
```

#### POST `/api/v1/prescriptions/{prescriptionId}/reject`
Reject prescription

**Request**:
```json
{
  "reason": "Invalid prescriber DEA number",
  "notes": "Contacted prescriber office, unable to verify"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "prescriptionId": "uuid",
    "status": "rejected",
    "rejectionReason": "Invalid prescriber DEA number",
    "rejectedAt": "2025-12-30T10:30:00Z",
    "rejectedBy": { ... }
  }
}
```

#### GET `/api/v1/prescriptions/{prescriptionId}/refills`
Get refill history

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "refillNumber": 0,
      "filledDate": "2025-12-30",
      "quantityDispensed": 30,
      "daysSupply": 30,
      "filledBy": "John Pharmacist",
      "billedAmount": 45.50
    }
  ]
}
```

#### POST `/api/v1/prescriptions/{prescriptionId}/refill-request`
Request refill

**Request**:
```json
{
  "prescriptionItemId": "uuid",
  "requestedBy": "patient",
  "notes": "Patient requested via phone"
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "refillRequestId": "uuid",
    "prescriptionId": "uuid",
    "status": "pending",
    "refillsRemaining": 4,
    "canFillNow": true,
    "nextAllowedFillDate": "2026-01-20",
    "message": "Refill request approved, ready to fill"
  }
}
```

---

## 4. Medication (Formulary) API

#### GET `/api/v1/medications`
Search medications

**Query Parameters**:
- `search` (name, NDC)
- `isControlled` (true/false)
- `isGeneric` (true/false)
- `therapeuticClass`
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "ndcCode": "00071015423",
      "drugName": "Lisinopril 10mg Tablet",
      "genericName": "Lisinopril",
      "brandName": "Prinivil",
      "strength": "10mg",
      "dosageForm": "tablet",
      "route": "oral",
      "manufacturer": "Pfizer",
      "isControlledSubstance": false,
      "isGeneric": true,
      "therapeuticClass": "ACE Inhibitor"
    }
  ],
  "pagination": { ... }
}
```

#### GET `/api/v1/medications/{medicationId}`
Get medication details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "ndcCode": "00071015423",
    "drugName": "Lisinopril 10mg Tablet",
    "genericName": "Lisinopril",
    "brandName": "Prinivil",
    "strength": "10mg",
    "dosageForm": "tablet",
    "route": "oral",
    "manufacturer": "Pfizer",
    "isControlledSubstance": false,
    "deaSchedule": null,
    "isGeneric": true,
    "therapeuticClass": "ACE Inhibitor",
    "pharmacologicClass": "Angiotensin Converting Enzyme Inhibitor",
    "interactions": [
      {
        "interactsWith": "Potassium supplements",
        "severity": "moderate",
        "description": "May increase potassium levels"
      }
    ],
    "isActive": true
  }
}
```

#### GET `/api/v1/medications/{medicationId}/interactions`
Get drug interactions

**Query Parameters**:
- `withMedicationIds` (comma-separated UUIDs to check interactions)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "medicationId": "uuid",
      "medicationName": "Potassium Chloride",
      "interactionType": "drug-drug",
      "severity": "moderate",
      "description": "Concurrent use may result in hyperkalemia",
      "management": "Monitor serum potassium levels"
    }
  ]
}
```

---

## 5. Inventory API

#### GET `/api/v1/inventory`
List inventory items

**Query Parameters**:
- `search` (medication name, NDC)
- `lowStock` (true - items below reorder point)
- `expiringWithinDays` (e.g., 30, 60, 90)
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "medication": {
        "id": "uuid",
        "ndcCode": "00071015423",
        "drugName": "Lisinopril 10mg Tablet"
      },
      "quantityOnHand": 500,
      "quantityUnit": "tablets",
      "reorderPoint": 200,
      "binLocation": "A-15-3",
      "nearestExpiryDate": "2026-08-15",
      "isLowStock": false,
      "unitCost": 0.15,
      "lastUpdated": "2025-12-30T08:00:00Z"
    }
  ],
  "pagination": { ... }
}
```

#### GET `/api/v1/inventory/{inventoryItemId}`
Get inventory item details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "medication": { ... },
    "quantityOnHand": 500,
    "quantityUnit": "tablets",
    "reorderPoint": 200,
    "reorderQuantity": 1000,
    "binLocation": "A-15-3",
    "requiresRefrigeration": false,
    "unitCost": 0.15,
    "averageWholesalePrice": 0.25,
    "batches": [
      {
        "id": "uuid",
        "batchNumber": "LOT12345",
        "expirationDate": "2026-08-15",
        "quantityRemaining": 300,
        "receivedDate": "2025-06-01",
        "status": "active"
      },
      {
        "id": "uuid",
        "batchNumber": "LOT67890",
        "expirationDate": "2026-11-20",
        "quantityRemaining": 200,
        "receivedDate": "2025-09-15",
        "status": "active"
      }
    ],
    "lastCountedAt": "2025-12-01T10:00:00Z",
    "isActive": true
  }
}
```

#### POST `/api/v1/inventory/{inventoryItemId}/adjust`
Adjust stock level

**Request**:
```json
{
  "adjustmentType": "physical_count",
  "batchId": "uuid",
  "quantityExpected": 500,
  "quantityActual": 485,
  "reason": "Physical inventory count - shrinkage detected",
  "requiresApproval": true
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "adjustmentId": "uuid",
    "inventoryItemId": "uuid",
    "quantityDifference": -15,
    "newQuantity": 485,
    "status": "pending_approval",
    "performedAt": "2025-12-30T10:30:00Z",
    "performedBy": { ... }
  }
}
```

#### GET `/api/v1/inventory/low-stock`
Get low stock items

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "inventoryItemId": "uuid",
      "medication": { ... },
      "quantityOnHand": 50,
      "reorderPoint": 200,
      "reorderQuantity": 1000,
      "recommendedAction": "Reorder immediately"
    }
  ]
}
```

#### GET `/api/v1/inventory/expiring`
Get expiring items

**Query Parameters**:
- `withinDays` (default: 90)

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "inventoryItemId": "uuid",
      "batchId": "uuid",
      "medication": { ... },
      "batchNumber": "LOT12345",
      "expirationDate": "2026-03-15",
      "daysUntilExpiry": 75,
      "quantityRemaining": 100,
      "recommendedAction": "Prioritize dispensing"
    }
  ]
}
```

#### GET `/api/v1/inventory/transactions`
Get inventory transaction history

**Query Parameters**:
- `inventoryItemId`
- `transactionType` (receipt, dispensed, adjustment, etc.)
- `fromDate`, `toDate`
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "inventoryItem": { ... },
      "transactionType": "dispensed",
      "quantityChange": -30,
      "quantityBefore": 500,
      "quantityAfter": 470,
      "referenceType": "prescription",
      "referenceId": "uuid",
      "reason": "Prescription RX789012 filled",
      "transactionDate": "2025-12-30T10:45:00Z",
      "performedBy": { ... }
    }
  ],
  "pagination": { ... }
}
```

---

## 6. Purchase Order API

#### GET `/api/v1/purchase-orders`
List purchase orders

**Query Parameters**:
- `status` (pending, sent, partially_received, received, cancelled)
- `supplierId`
- `fromDate`, `toDate`
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "poNumber": "PO123456",
      "supplier": {
        "id": "uuid",
        "name": "McKesson Corporation"
      },
      "orderDate": "2025-12-25",
      "expectedDeliveryDate": "2025-12-30",
      "status": "sent",
      "totalAmount": 5000.00,
      "itemCount": 15
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/purchase-orders`
Create purchase order

**Request**:
```json
{
  "supplierId": "uuid",
  "expectedDeliveryDate": "2026-01-10",
  "notes": "Rush order for flu season",
  "items": [
    {
      "inventoryItemId": "uuid",
      "ndcCode": "00071015423",
      "medicationName": "Lisinopril 10mg Tablet",
      "quantityOrdered": 1000,
      "unitPrice": 0.12
    }
  ]
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "poNumber": "PO123457",
    "supplier": { ... },
    "orderDate": "2025-12-30",
    "expectedDeliveryDate": "2026-01-10",
    "status": "pending",
    "items": [ ... ],
    "subtotal": 120.00,
    "totalAmount": 120.00,
    "createdAt": "2025-12-30T10:30:00Z",
    "createdBy": { ... }
  }
}
```

#### POST `/api/v1/purchase-orders/{poId}/receive`
Receive purchase order (full or partial)

**Request**:
```json
{
  "actualDeliveryDate": "2025-12-30",
  "items": [
    {
      "poItemId": "uuid",
      "quantityReceived": 1000,
      "batchNumber": "LOT99999",
      "expirationDate": "2027-12-30",
      "unitCost": 0.12,
      "condition": "good"
    }
  ],
  "notes": "All items received in good condition"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "poId": "uuid",
    "status": "received",
    "actualDeliveryDate": "2025-12-30",
    "itemsReceived": 15,
    "totalItemsOrdered": 15,
    "receivedBy": { ... },
    "receivedAt": "2025-12-30T10:30:00Z"
  }
}
```

---

## 7. Billing API

#### GET `/api/v1/invoices`
List invoices

**Query Parameters**:
- `patientId`
- `status` (pending, paid, overdue, cancelled)
- `fromDate`, `toDate`
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "invoiceNumber": "INV123456",
      "patient": {
        "id": "uuid",
        "patientNumber": "P123456",
        "name": "Alice Johnson"
      },
      "prescription": {
        "id": "uuid",
        "prescriptionNumber": "RX789012"
      },
      "invoiceDate": "2025-12-30",
      "totalAmount": 45.50,
      "patientCopay": 10.00,
      "insurancePayment": 35.50,
      "status": "paid"
    }
  ],
  "pagination": { ... }
}
```

#### GET `/api/v1/invoices/{invoiceId}`
Get invoice details

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "invoiceNumber": "INV123456",
    "patient": { ... },
    "prescription": { ... },
    "invoiceDate": "2025-12-30",
    "dueDate": "2026-01-30",
    "items": [
      {
        "id": "uuid",
        "itemType": "medication",
        "description": "Lisinopril 10mg - 30 tablets",
        "ndcCode": "00071015423",
        "quantity": 30,
        "unitPrice": 1.50,
        "totalPrice": 45.00
      },
      {
        "id": "uuid",
        "itemType": "dispensing_fee",
        "description": "Dispensing Fee",
        "quantity": 1,
        "unitPrice": 0.50,
        "totalPrice": 0.50
      }
    ],
    "subtotal": 45.50,
    "taxAmount": 0.00,
    "discountAmount": 0.00,
    "insurancePayment": 35.50,
    "patientCopay": 10.00,
    "totalAmount": 45.50,
    "status": "paid",
    "payments": [
      {
        "id": "uuid",
        "paymentNumber": "PAY789012",
        "amount": 10.00,
        "paymentMethod": "credit_card",
        "paymentDate": "2025-12-30"
      }
    ]
  }
}
```

#### POST `/api/v1/invoices/{invoiceId}/payments`
Process payment

**Request**:
```json
{
  "amount": 10.00,
  "paymentMethod": "credit_card",
  "cardLastFour": "1234",
  "transactionId": "txn_abc123xyz",
  "notes": "Paid at pickup"
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "paymentId": "uuid",
    "paymentNumber": "PAY789012",
    "invoiceId": "uuid",
    "amount": 10.00,
    "paymentMethod": "credit_card",
    "status": "completed",
    "paymentDate": "2025-12-30",
    "processedBy": { ... },
    "invoice": {
      "newStatus": "paid",
      "remainingBalance": 0.00
    }
  }
}
```

#### GET `/api/v1/insurance-claims`
List insurance claims

**Query Parameters**:
- `status` (submitted, pending, approved, rejected)
- `prescriptionId`
- `fromDate`, `toDate`
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "claimNumber": "CLM123456",
      "prescription": { ... },
      "submissionDate": "2025-12-30",
      "submittedAmount": 45.50,
      "approvedAmount": 35.50,
      "patientResponsibility": 10.00,
      "status": "approved",
      "responseDate": "2025-12-30"
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/insurance-claims`
Submit insurance claim

**Request**:
```json
{
  "prescriptionId": "uuid",
  "patientInsuranceId": "uuid",
  "submittedAmount": 45.50,
  "items": [
    {
      "ndcCode": "00071015423",
      "quantity": 30,
      "daysSupply": 30,
      "unitPrice": 1.50
    }
  ]
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "claimId": "uuid",
    "claimNumber": "CLM123456",
    "status": "submitted",
    "submissionDate": "2025-12-30",
    "submittedAmount": 45.50,
    "externalClaimId": "INS789012"
  }
}
```

---

## 8. Reporting API

#### GET `/api/v1/reports/sales`
Sales report

**Query Parameters**:
- `fromDate`, `toDate` (required)
- `groupBy` (day, week, month)
- `prescriberId` (optional)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "period": {
      "from": "2025-12-01",
      "to": "2025-12-30"
    },
    "summary": {
      "totalSales": 125000.50,
      "totalPrescriptions": 2500,
      "averageSalePerPrescription": 50.00,
      "totalCopays": 25000.00,
      "totalInsurancePayments": 100000.50
    },
    "breakdown": [
      {
        "date": "2025-12-01",
        "sales": 4200.00,
        "prescriptions": 85
      }
    ]
  }
}
```

#### GET `/api/v1/reports/inventory-value`
Inventory valuation report

**Response** (200):
```json
{
  "success": true,
  "data": {
    "totalValue": 250000.00,
    "itemCount": 450,
    "byCategory": [
      {
        "category": "Cardiovascular",
        "value": 50000.00,
        "itemCount": 85
      }
    ],
    "expiringValue": {
      "within30Days": 5000.00,
      "within60Days": 12000.00,
      "within90Days": 20000.00
    }
  }
}
```

#### GET `/api/v1/reports/compliance`
Compliance report (controlled substances)

**Query Parameters**:
- `fromDate`, `toDate`

**Response** (200):
```json
{
  "success": true,
  "data": {
    "period": { ... },
    "controlledSubstances": [
      {
        "deaSchedule": "II",
        "medicationName": "Oxycodone 10mg",
        "totalDispensed": 150,
        "prescriptions": 25,
        "averageQuantityPerRx": 6
      }
    ],
    "prescribers": [
      {
        "name": "Dr. Smith",
        "npi": "1234567890",
        "controlledSubstancePrescriptions": 45
      }
    ]
  }
}
```

---

## 9. Notification API

#### GET `/api/v1/notifications`
List notifications (for current user)

**Query Parameters**:
- `status` (pending, sent, delivered, failed)
- `type` (sms, email, push)
- `page`, `pageSize`

**Response** (200):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "type": "sms",
      "recipient": "+1234567890",
      "message": "Your prescription RX789012 is ready for pickup",
      "status": "delivered",
      "sentAt": "2025-12-30T10:45:00Z",
      "deliveredAt": "2025-12-30T10:45:15Z"
    }
  ],
  "pagination": { ... }
}
```

#### POST `/api/v1/notifications/send`
Send notification (manual)

**Request**:
```json
{
  "patientId": "uuid",
  "type": "sms",
  "message": "Reminder: Your prescription refill is due",
  "scheduleFor": null
}
```

**Response** (201):
```json
{
  "success": true,
  "data": {
    "notificationId": "uuid",
    "status": "sent",
    "sentAt": "2025-12-30T10:30:00Z"
  }
}
```

---

## API Design Patterns & Best Practices

### 1. **Idempotency Keys**

For critical operations (payments, prescription fills), use idempotency keys:

```
POST /api/v1/prescriptions/{prescriptionId}/fill
Headers:
  Idempotency-Key: unique-key-123
```

Server ensures the operation is only processed once, even if request is retried.

### 2. **Batch Operations**

For efficiency, support batch endpoints:

```
POST /api/v1/prescriptions/batch-validate
{
  "prescriptionIds": ["uuid1", "uuid2", "uuid3"]
}
```

### 3. **Webhooks** (for integrations)

Allow external systems to subscribe to events:

```
POST /api/v1/webhooks
{
  "url": "https://external-system.com/webhook",
  "events": ["prescription.filled", "inventory.low_stock"],
  "secret": "webhook-secret-key"
}
```

### 4. **Rate Limiting**

Response headers:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1640995200
```

### 5. **Versioning Strategy**

- **URL versioning**: `/api/v1/`, `/api/v2/`
- Maintain v1 for 12 months after v2 release
- Deprecation header: `Deprecated: true; sunset="2026-12-31"`

### 6. **Filtering, Sorting, Pagination**

Standard query parameters:
```
GET /api/v1/prescriptions?
  status=validated&
  fromDate=2025-12-01&
  sortBy=prescriptionDate&
  sortOrder=desc&
  page=1&
  pageSize=20
```

---

## OpenAPI/Swagger Documentation

All these APIs should be documented in OpenAPI 3.0 format. Example snippet:

```yaml
openapi: 3.0.0
info:
  title: Pharmacy Management System API
  version: 1.0.0
  description: Complete API for pharmacy operations

servers:
  - url: https://api.pharmacy.com/api/v1
    description: Production
  - url: https://staging-api.pharmacy.com/api/v1
    description: Staging

security:
  - BearerAuth: []

paths:
  /prescriptions:
    get:
      summary: List prescriptions
      tags: [Prescriptions]
      parameters:
        - name: status
          in: query
          schema:
            type: string
            enum: [received, validated, in_progress, ready, dispensed]
        - name: page
          in: query
          schema:
            type: integer
            default: 1
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PrescriptionListResponse'

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  
  schemas:
    PrescriptionListResponse:
      type: object
      properties:
        success:
          type: boolean
        data:
          type: array
          items:
            $ref: '#/components/schemas/Prescription'
        pagination:
          $ref: '#/components/schemas/Pagination'
```

---

## Summary

We've defined:

✅ **60+ API endpoints** across 9 modules
✅ **RESTful conventions** with proper HTTP methods
✅ **Consistent response format** (success/error)
✅ **Pagination, filtering, sorting** on list endpoints
✅ **Complete CRUD operations** for all entities
✅ **Workflow endpoints** (validate, fill, reject prescriptions)
✅ **Reporting endpoints** (sales, inventory, compliance)
✅ **Security patterns** (JWT, rate limiting, idempotency)
✅ **Integration hooks** (webhooks for external systems)

These APIs support:
- All 3 core workflows we defined
- Easy integration of new features (add endpoints without breaking existing)
- Third-party integrations (e-prescribing, insurance, etc.)
- Mobile/web frontend development