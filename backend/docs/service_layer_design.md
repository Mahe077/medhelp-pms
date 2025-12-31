# Service Layer Design

This document outlines the organization of business logic in the Pharmacy Management System. We follow a Domain-Driven Design (DDD) approach within a Modular Monolith.

## Service Types

### 1. Application Services
- **Location**: `com.medhelp.pms.modules.[module].application`
- **Responsibility**:
    - Orchestrate business flows.
    - Manage transactions (`@Transactional`).
    - Security checks (RBAC).
    - Map between Entities and DTOs.
    - Interact with Infrastructure (Repositories, Messaging).
- **Rule**: Should not contain complex business logic. They "tell" the domain what to do.

### 2. Domain Services
- **Location**: `com.medhelp.pms.modules.[module].domain.service`
- **Responsibility**:
    - Encapsulate business logic that involves multiple entities.
    - Handle complex calculations or validations that don't belong to a single entity.
    - Agnostic of technical details (DB, REST, etc.).
- **Rule**: Used when logic doesn't naturally fit into an Entity or Value Object.

## Repository Pattern
- **Location**: `com.medhelp.pms.modules.[module].domain.repository` (Interface)
- **Implementation**: `com.medhelp.pms.modules.[module].infrastructure.persistence`
- **Responsibility**: Provide a collection-like interface for accessing aggregates.

## Communication Pattern
- **Internal**: Modules should communicate via **Domain Events** (Events published within the same JVM).
- **External**: (Future) Move to Message Broker (RabbitMQ) for asynchronous processing if needed.

## Example Flow
1. `RestController` receives request.
2. `ApplicationService` begins transaction.
3. `ApplicationService` loads Aggregate via `Repository`.
4. `ApplicationService` invokes logic on `Entity` or `DomainService`.
5. `Entity` performs logic and records a `DomainEvent`.
6. `ApplicationService` saves Aggregate.
7. Transaction commits.
8. `EventPublisher` dispatches events to listeners in other modules.
