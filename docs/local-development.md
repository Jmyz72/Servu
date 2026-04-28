# Local Development

## PostgreSQL

Create a local database named `servu` if it does not already exist:

```bash
createdb servu
```

Set backend credentials before running Spring Boot:

```bash
export DB_USERNAME=your_postgres_user
export DB_PASSWORD=your_postgres_password
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
