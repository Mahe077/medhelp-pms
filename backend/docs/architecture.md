## Proposed Architecture: Event-Driven Modular Monolith

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Layer                             │
│  (Web App, Mobile App, Desktop, Third-party Integrations)   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway / BFF                         │
│  - Authentication & Authorization (JWT)                      │
│  - Rate Limiting & Throttling                               │
│  - Request Routing                                          │
│  - API Versioning                                           │
│  - Input Validation                                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Application Layer                           │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐           │
│  │Prescription│  │ Inventory  │  │  Patient   │           │
│  │  Module    │  │   Module   │  │   Module   │  ...      │
│  │            │  │            │  │            │           │
│  │ Domain ────┼──┼── Domain ──┼──┼── Domain ──┼──┐        │
│  │ Logic      │  │   Logic    │  │   Logic    │  │        │
│  └────────────┘  └────────────┘  └────────────┘  │        │
│         │               │               │         │        │
│         └───────────────┼───────────────┘         │        │
│                         │                         │        │
│                         ▼                         │        │
│              ┌─────────────────────┐              │        │
│              │   Event Bus Core    │◄─────────────┘        │
│              │  (In-Process Queue) │                       │
│              └─────────────────────┘                       │
│                         │                                  │
└─────────────────────────┼──────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│              Persistence Layer                               │
│                                                              │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────┐    │
│  │   PostgreSQL   │  │    Redis     │  │  File Store │    │
│  │                │  │              │  │             │    │
│  │ - Transactional│  │ - Cache      │  │ - Documents │    │
│  │   Data         │  │ - Sessions   │  │ - Images    │    │
│  │ - Schema per   │  │ - Temp Data  │  │ - Reports   │    │
│  │   Module       │  │              │  │             │    │
│  └────────────────┘  └──────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│           External Integration Layer                         │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐           │
│  │ Insurance  │  │  Payment   │  │ E-Prescribe│           │
│  │ Providers  │  │ Gateways   │  │  Systems   │  ...      │
│  └────────────┘  └────────────┘  └────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Architectural Decisions & Reasoning

### 1. **Modular Monolith (Not Microservices)**

**Decision**: Build as a single deployable application with strong module boundaries.

**Reasons**:

✅ **Simplicity for Your Context**
- Requirements are vague and evolving
- You don't have multiple teams working independently
- Deployment complexity of microservices isn't justified yet
- Single database makes transactions easier

✅ **Faster Development**
- No network calls between modules (in-process communication)
- Easier debugging (single call stack)
- Simpler deployment pipeline
- Refactoring across modules is straightforward

✅ **Economic Sense**
- Lower infrastructure costs (one server initially vs many)
- Easier to monitor and maintain
- Don't need Kubernetes/orchestration immediately

✅ **Future-Proof**
- Can extract modules to microservices later if needed
- Well-defined boundaries make extraction clean
- Start simple, complexity when justified

**When to Reconsider**: If you get to 50k+ prescriptions/day, multiple development teams, or specific modules need independent scaling.

---

### 2. **Event-Driven Architecture (Within the Monolith)**

**Decision**: Modules communicate primarily through domain events via an in-process event bus.

**Reasons**:

✅ **Loose Coupling**
- Modules don't directly call each other
- Can add new modules without modifying existing ones
- Example: Add a "Loyalty Points" module that subscribes to `PrescriptionFilled` events without touching prescription code

✅ **Handles Vague Requirements**
- New features often react to existing events
- Don't need to predict all integrations upfront
- Example: Later add "SMS notifications when prescription ready" by subscribing to events

✅ **Natural Audit Trail**
- Every important action generates an event
- Events are stored (Event Sourcing lite)
- Compliance and debugging made easier

✅ **Async Processing**
- Non-critical tasks don't slow down main flow
- Example: Prescription filled → immediately respond to user, send notifications later

✅ **Testability**
- Test modules in isolation by publishing fake events
- Can replay events to reproduce bugs

**Example Flow**:
```
User fills prescription
  → Prescription Module emits: PrescriptionFilled event
    → Inventory Module consumes: Deducts stock
    → Billing Module consumes: Creates invoice  
    → Patient Module consumes: Updates history
    → Notification Module consumes: Sends SMS
```

Each consumer works independently. If Notification fails, others aren't affected.

**Implementation**: Start with in-memory event bus (like MediatR pattern), later upgrade to RabbitMQ/Kafka if you need durability/distribution.

---

### 3. **Domain-Driven Design (DDD) Principles**

**Decision**: Organize code around business domains (bounded contexts) with rich domain models.

**Reasons**:

✅ **Matches Business Reality**
- Code structure mirrors pharmacy operations
- Business experts can understand code organization
- Easier onboarding for new developers

✅ **Encapsulates Complexity**
- Business rules live in domain objects, not scattered
- Example: `Prescription.Validate()` contains all validation logic in one place

✅ **Manages Vague Requirements**
- Clear places to add new features
- Domain experts guide where code goes
- Example: New regulation? Goes in Prescription domain

✅ **Ubiquitous Language**
- Code uses pharmacy terminology (Prescription, Dispense, Formulary)
- Reduces translation errors between business and code

**Structure Per Module**:
```
prescription-module/
├── domain/
│   ├── entities/          (Prescription, Medication)
│   ├── value-objects/     (DosageAmount, DrugCode)
│   ├── aggregates/        (Prescription with items)
│   ├── domain-events/     (PrescriptionFilled)
│   ├── repositories/      (interfaces)
│   └── domain-services/   (complex business logic)
├── application/
│   ├── use-cases/         (FillPrescriptionUseCase)
│   ├── dtos/              (data transfer objects)
│   └── interfaces/        (ports)
├── infrastructure/
│   ├── persistence/       (database implementation)
│   ├── external-services/ (API clients)
│   └── messaging/         (event publishers)
└── api/
    ├── controllers/       (REST endpoints)
    └── validators/        (input validation)
```

---

### 4. **CQRS (Command Query Responsibility Segregation)**

**Decision**: Separate read and write operations, especially for reporting.

**Reasons**:

✅ **Different Access Patterns**
- Writes: Complex validation, transactions (prescription filling)
- Reads: Simple lookups, aggregations, reports (inventory dashboard)

✅ **Performance Optimization**
- Can optimize read models independently
- Use materialized views for complex reports
- Cache reads aggressively without affecting writes

✅ **Scalability**
- Most pharmacy operations are read-heavy (lookups)
- Can add read replicas without complicating write logic

**Not Full CQRS**: We're not doing separate databases (CQRS Lite). Just separate models and paths.

**Example**:
```
Write Side:
POST /prescriptions → Complex validation → Update database

Read Side:  
GET /prescriptions/{id} → Simple query from read-optimized view
GET /reports/inventory → Query materialized view
```

---

### 5. **API Gateway / BFF (Backend for Frontend)**

**Decision**: Single entry point for all client requests.

**Reasons**:

✅ **Security Centralization**
- Authentication/authorization in one place
- Rate limiting to prevent abuse
- API key management

✅ **Client Flexibility**
- Web app needs different data shape than mobile
- Can create specific BFF endpoints per client
- GraphQL option later if needed

✅ **Cross-Cutting Concerns**
- Logging, monitoring, tracing
- Request/response transformation
- API versioning (/v1/, /v2/)

✅ **Simplifies Modules**
- Modules don't worry about authentication
- Gateway handles it, passes validated user context

**Alternative Considered**: Direct module access
**Why Rejected**: Security scattered, harder to evolve APIs

---

### 6. **PostgreSQL as Primary Database**

**Decision**: Single PostgreSQL instance with separate schemas per module.

**Reasons**:

✅ **ACID Transactions**
- Pharmacy operations need consistency
- Example: Prescription fill + inventory deduction must be atomic
- Can't have "prescription filled but stock not deducted"

✅ **Relational Data Model**
- Pharmacy data is highly relational (prescriptions → patients → medications)
- Foreign keys enforce referential integrity

✅ **Rich Query Capabilities**
- Complex reporting queries (sales by drug, expiry reports)
- Full-text search for medications
- JSON support for flexible fields (insurance details)

✅ **Mature Ecosystem**
- Excellent tooling, backup solutions
- Well understood by developers
- Battle-tested for decades

✅ **Schema Per Module**
- `prescription_schema`, `inventory_schema`, `patient_schema`
- Enforces module boundaries
- Can migrate schemas to separate databases later

**Alternatives Considered**:
- **MongoDB**: Rejected - need ACID, complex queries
- **MySQL**: Postgres has better JSON, extension ecosystem
- **DynamoDB**: Over-complicated for this use case

---

### 7. **Redis for Caching**

**Decision**: Use Redis for caching, sessions, and temporary data.

**Reasons**:

✅ **Frequently Accessed Data**
- Drug formulary (rarely changes, read often)
- Patient profiles during prescription filling
- User sessions

✅ **Performance**
- Sub-millisecond lookups
- Reduces database load significantly

✅ **Temporary Data**
- Prescription drafts (while pharmacist is reviewing)
- Shopping cart (if you add e-commerce later)

✅ **Rate Limiting**
- Track API usage per user
- Implement sliding window rate limits

**Cache Strategy**: Cache-aside pattern
```
1. Check Redis for data
2. If miss, query PostgreSQL
3. Store in Redis with TTL
4. Return data
```

---

### 8. **Message Queue (Future: RabbitMQ/Kafka)**

**Decision**: Start with in-memory event bus, migrate to durable queue when needed.

**Reasons for Starting Simple**:

✅ **Premature Optimization Avoidance**
- In-memory event bus is fast and simple
- No additional infrastructure to manage
- Perfect for initial development

**When to Add RabbitMQ/Kafka**:

✅ **Durability Needed**
- Events must survive application restarts
- Critical business events need persistence

✅ **High Volume**
- Processing thousands of events per second
- Need backpressure handling

✅ **Distributed System**
- When you extract services from monolith
- Services on different servers need to communicate

**My Recommendation**: Start in-memory, add RabbitMQ when you hit 1000+ events/minute or need guaranteed delivery.

---

### 9. **Layered Architecture Within Each Module**

**Decision**: Four-layer architecture within modules.

```
┌─────────────────────┐
│   API Layer         │ ← REST controllers, GraphQL resolvers
├─────────────────────┤
│ Application Layer   │ ← Use cases, orchestration
├─────────────────────┤
│  Domain Layer       │ ← Business logic, entities
├─────────────────────┤
│Infrastructure Layer │ ← Database, external APIs
└─────────────────────┘
```

**Reasons**:

✅ **Separation of Concerns**
- API changes don't affect business logic
- Can swap database without touching domain

✅ **Testability**
- Test domain logic without database
- Mock infrastructure in tests

✅ **Dependency Rule**
- Dependencies point inward (infrastructure → domain, never reverse)
- Domain has zero dependencies on external libraries

**Example - Filling a Prescription**:

```
API Layer:
POST /prescriptions/{id}/fill
  ↓
Application Layer:  
FillPrescriptionUseCase
  - Orchestrates workflow
  - Validates input
  - Calls domain logic
  - Publishes events
  ↓
Domain Layer:
Prescription.Fill()
  - Business rules
  - State transitions
  - Domain events
  ↓
Infrastructure Layer:
PrescriptionRepository.Save()
  - Persist to database
```

---

### 10. **Authentication & Authorization Strategy**

**Decision**: JWT-based authentication with Role-Based Access Control (RBAC).

**Reasons**:

✅ **Stateless**
- No session storage needed on server
- Scales horizontally easily
- JWT contains user claims (role, permissions)

✅ **Role-Based Access**
- Pharmacist: Can fill prescriptions
- Technician: Can receive inventory
- Manager: Can view reports
- Doctor: Can only send prescriptions (if integrated)

✅ **HIPAA Compliance**
- Audit who accessed what patient data
- Enforce minimum necessary access
- JWT claims logged with every action

**Implementation**:
```
User logs in
  → API Gateway validates credentials
  → Issues JWT with claims: {userId, role, permissions}
  → Client includes JWT in subsequent requests
  → API Gateway validates JWT
  → Passes user context to modules
```

---

## Why NOT Microservices? (Detailed Reasoning)

People often jump to microservices. Here's why it's wrong for you now:

❌ **Operational Complexity**
- Need: Service discovery, API gateway, distributed tracing, container orchestration
- Cost: Weeks of setup, ongoing maintenance burden
- Your context: Requirements are vague, need to iterate quickly

❌ **Distributed System Challenges**
- Network failures between services
- Eventual consistency complications
- Distributed transactions (sagas) are complex
- Debugging across services is harder

❌ **Premature Optimization**
- You don't have scaling problems yet
- "Optimize when you have the problem, not before"
- A monolith can handle 100k+ requests/day easily

❌ **Team Size**
- Microservices shine with multiple independent teams
- You likely have 1-5 developers
- More services = more context switching

**The Rule**: Start with a well-structured monolith. Extract services when you have a specific problem that microservices solve (team independence, specific scaling needs, polyglot persistence).

---

## Why Event-Driven? (Deeper Dive)

Let me illustrate with a concrete example:

**Scenario**: Prescription is filled.

**Without Events (Direct Calls)**:
```javascript
function fillPrescription(prescriptionId) {
  // Prescription module
  prescription.markFilled();
  prescriptionRepo.save(prescription);
  
  // Now what? Module must know about others:
  inventoryService.deductStock(prescription.items); // Coupling!
  billingService.createInvoice(prescription);        // Coupling!
  patientService.updateHistory(prescription);        // Coupling!
  notificationService.sendSMS(prescription);         // Coupling!
  
  // What if one fails? Rollback all?
  // What if we add loyalty points later? Modify this function!
}
```

**Problems**:
- Prescription module knows about all other modules
- Tight coupling
- Adding new features requires changing this code
- Transaction management nightmare
- One failure affects everything

**With Events**:
```javascript
// Prescription module
function fillPrescription(prescriptionId) {
  prescription.markFilled();
  prescriptionRepo.save(prescription);
  
  // Just publish event and forget
  eventBus.publish(new PrescriptionFilled(prescription));
}

// Inventory module (separate file, separate concern)
eventBus.subscribe(PrescriptionFilled, (event) => {
  inventory.deductStock(event.prescription.items);
});

// Billing module
eventBus.subscribe(PrescriptionFilled, (event) => {
  billing.createInvoice(event.prescription);
});

// Patient module  
eventBus.subscribe(PrescriptionFilled, (event) => {
  patient.updateMedicationHistory(event.prescription);
});

// Later, add Loyalty module without touching prescription code!
eventBus.subscribe(PrescriptionFilled, (event) => {
  loyalty.awardPoints(event.prescription);
});
```

**Benefits**:
- Prescription module knows nothing about others
- Add loyalty points without changing prescription code
- Each subscriber handles its own errors
- Perfect for vague requirements ("we might add X later")

---

## Scalability Considerations Built-In

Even though we're starting as a monolith, the architecture enables scaling:

**Horizontal Scaling**:
- Stateless application servers
- Load balancer in front
- Shared PostgreSQL and Redis

**Database Scaling**:
- Read replicas for reports
- Connection pooling
- Partitioning large tables (prescriptions by date)

**Caching Strategy**:
- Cache expensive queries
- Invalidate cache on writes via events

**Async Processing**:
- Move slow operations off request path
- Example: Insurance claims processed async

**Extract to Microservices When Needed**:
- Well-defined module boundaries make extraction clean
- Event-driven means services can communicate same way

---

## Migration Path (Future)

Your architecture naturally evolves:

**Phase 1: Monolith** (Now - 1 year)
- Single deployment
- In-memory event bus
- One database

**Phase 2: Modular Monolith with Message Queue** (Year 1-2)
- Add RabbitMQ for durable events
- Still single deployment
- Separate read replicas

**Phase 3: Extract High-Load Services** (Year 2-3)
- Extract Notification service (lots of traffic, can fail independently)
- Extract Insurance service (slow external API, shouldn't block main flow)
- Monolith + 2 microservices

**Phase 4: Full Microservices** (If needed)
- Extract remaining modules as needed
- Event bus becomes distributed (Kafka)
- API Gateway routes to multiple services

---

## Technology Stack Recommendation

Based on this architecture:

**Backend**:
- **Language**: Node.js/TypeScript or .NET Core or Java Spring Boot
- **Framework**: Express/NestJS or ASP.NET or Spring
- **ORM**: TypeORM/Prisma or Entity Framework or Hibernate

**Database**:
- **Primary**: PostgreSQL 14+
- **Cache**: Redis 7+
- **Search**: PostgreSQL full-text (start), Elasticsearch (later if needed)

**Message Queue**:
- **Start**: In-memory (MediatR pattern)
- **Later**: RabbitMQ or AWS SQS

**API**:
- **Style**: REST (start), GraphQL (if frontend complexity demands)
- **Documentation**: OpenAPI/Swagger

**Authentication**:
- **Strategy**: JWT with refresh tokens
- **Implementation**: Passport.js or IdentityServer

**Monitoring** (Day 1):
- **Logging**: Winston/Serilog with structured logging
- **Metrics**: Prometheus + Grafana
- **Tracing**: OpenTelemetry (future)

---

## Summary: Architecture Decisions Table

| Decision | Choice | Why | Alternative Considered |
|----------|--------|-----|----------------------|
| **Overall Style** | Modular Monolith | Simplicity, faster development, requirements evolving | Microservices (too complex now) |
| **Communication** | Event-Driven | Loose coupling, easy to add features | Direct calls (tight coupling) |
| **Design Approach** | Domain-Driven Design | Matches business, manages complexity | Transaction Script (gets messy) |
| **Database** | PostgreSQL (single) | ACID, relational, mature | MongoDB (need transactions), DB per service (overkill) |
| **Caching** | Redis | Performance, sessions | Memcached (less features) |
| **Message Queue** | In-memory → RabbitMQ | Start simple, add when needed | Kafka (too much overhead now) |
| **API Style** | REST | Simple, well-understood | GraphQL (unnecessary complexity now) |
| **Auth** | JWT + RBAC | Stateless, scalable | Session-based (doesn't scale) |