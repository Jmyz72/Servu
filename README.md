# Servu

Servu is a QR food ordering platform scaffolded as a monorepo with a React + TypeScript frontend, a Spring Boot backend, and a local PostgreSQL database named `servu`.

## Project Structure

```text
frontend/  React + TypeScript app
backend/   Spring Boot API
docs/      Project notes
infra/     Future infrastructure files
```

## Prerequisites

- Node.js and npm
- Java 21 or newer
- PostgreSQL running locally on port `5432`
- A PostgreSQL database named `servu`

## Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

The frontend runs at `http://localhost:5173` and expects the backend API at `VITE_API_BASE_URL`, defaulting to `http://localhost:8080`.

## Backend

```bash
cd backend
cp .env.example .env
export DB_USERNAME=your_postgres_user
export DB_PASSWORD=your_postgres_password
./mvnw spring-boot:run
```

The backend runs at `http://localhost:8080`.

Health check:

```bash
curl http://localhost:8080/api/health
```

## Tests

```bash
cd frontend
npm run build

cd ../backend
./mvnw test
```
