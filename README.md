# Pharmacy Management System (PMS)

A modern, event-driven modular monolith for pharmacy operations built with Spring Boot and Next.js.

## Architecture

- **Backend**: Spring Boot 4.0 with DDD-inspired modular architecture
- **Frontend**: Next.js 16 with TypeScript and Tailwind CSS
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Deployment**: Docker & Docker Compose

## Quick Start

### Prerequisites

- Docker and Docker Compose
- (Optional) Java 17 and Node.js 20 for local development

### Running with Docker

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd pms
   ```

2. **Create environment file**

   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start all services**

   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Health: http://localhost:8080/actuator/health

### Stopping Services

```bash
docker-compose down
```

To remove volumes (database data):

```bash
docker-compose down -v
```

## Development

### Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend (Next.js)

```bash
cd frontend
npm install
npm run dev
```

## Project Structure

```
pms/
├── backend/              # Spring Boot application
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/medhelp/pms/
│   │               └── modules/
│   │                   ├── identity_module/
│   │                   ├── inventory_module/
│   │                   ├── patient_module/
│   │                   ├── prescription_module/
│   │                   ├── billing_module/
│   │                   └── notification_module/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/             # Next.js application
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml    # Service orchestration
└── .env.example          # Environment template
```

## Module Architecture

Each module follows DDD principles:

- `domain/`: Entities, value objects, aggregates, domain events
- `application/`: Use cases, DTOs, interfaces
- `infrastructure/`: Persistence, external services, messaging
- `api/`: REST controllers, validators

## Environment Variables

See `.env.example` for all available configuration options.

## License

[Your License Here]
