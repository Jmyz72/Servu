# Local Development

## PostgreSQL

Start PostgreSQL if it is not already running:

```bash
brew services start postgresql@18
```

Create a local database named `servu` if it does not already exist:

```bash
createdb servu
```

Set backend credentials before running Spring Boot:

```bash
export DB_USERNAME=your_postgres_user
export DB_PASSWORD=your_postgres_password
```

For a default Homebrew PostgreSQL setup on macOS, this may be enough:

```bash
export DB_USERNAME=$(whoami)
export DB_PASSWORD=
```

## Run the Apps

Terminal 1:

```bash
cd frontend
npm run dev
```

Terminal 2:

```bash
cd backend
./mvnw spring-boot:run
```

Frontend: `http://localhost:5173`

Backend: `http://localhost:8080`

Health check: `http://localhost:8080/api/health`

## Backend Startup Verification

To verify Spring Boot, PostgreSQL, and Flyway together:

```bash
cd backend
DB_USERNAME=$(whoami) DB_PASSWORD= ./mvnw spring-boot:run
```

In another terminal:

```bash
curl http://localhost:8080/api/health
```

Expected response includes:

```json
{
  "status": "UP",
  "service": "servu-backend"
}
```

## Useful Commands

```bash
cd frontend
npm run build
npm run lint
```

```bash
cd backend
./mvnw test
```
