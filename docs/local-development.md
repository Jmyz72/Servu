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
export SERVU_ADMIN_EMAIL=admin@example.com
export SERVU_ADMIN_PASSWORD=change-this-password
export SERVU_ADMIN_DISPLAY_NAME="Platform Admin"
```

For a default Homebrew PostgreSQL setup on macOS, this may be enough:

```bash
export DB_USERNAME=$(whoami)
export DB_PASSWORD=
export SERVU_ADMIN_EMAIL=admin@example.com
export SERVU_ADMIN_PASSWORD=change-this-password
```

`SERVU_ADMIN_PASSWORD` must be at least 12 characters. On startup, the backend creates the platform admin if the email does not already exist; it does not overwrite an existing user's password.

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

Login: `http://localhost:5173/login`

## Backend Startup Verification

To verify Spring Boot, PostgreSQL, and Flyway together:

```bash
cd backend
DB_USERNAME=$(whoami) DB_PASSWORD= SERVU_ADMIN_EMAIL=admin@example.com SERVU_ADMIN_PASSWORD=change-this-password ./mvnw spring-boot:run
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
