# Peepol

Peepol is a comprehensive person management platform designed to be robust, secure, and easy to deploy. This repository contains both the backend (API) and the frontend (Web).

## 🚀 Quick Start

The simplest way to get the entire ecosystem up and running is by using **Docker Compose**. This will automatically configure the PostgreSQL database, the Backend service, and the Frontend application.

### Initialization Steps:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/jelvi00/peepol.git
    cd peepol
    ```

2.  **Configure the environment**:
    Copy the sample environment variables file to a new `.env` file:
    ```bash
    cp .env.sample .env
    ```
    *Please change `.env` values to desired ones.*

3.  **Build and start the containers**:
    ```bash
    docker compose --env-file .env up -d
    ```

4.  **Access the application**:
    - **Frontend (Web)**: [http://localhost:3000](http://localhost:3000)
    - **Backend (API)**: [http://localhost:4556/peepol-api](http://localhost:4556/peepol-api)

---

## 🛠️ Technologies Used

### Backend
- **Java 25** with **Spring Boot 3.5**.
- **Spring Security** with **PASETO** (Platform-Agnostic Security Tokens) authentication.
- **Liquibase** for database version control.
- **PostgreSQL 16** as the main database.
- **Gradle** as the dependency and build manager.

### Frontend
- **Next.js 16** (App Router) and **React 19**.
- **Tailwind CSS 4** for styling.
- **Pnpm** as the package manager.
- **Jest** and **React Testing Library** for unit testing.
- **Playwright** for end-to-end (E2E) testing.

---

## 🔐 Authentication and Users

The system includes an administrator user automatically created through database migrations.

- **Default System User**: `[configure it in .env]`
- **Registration**: New users can be registered through the API with ADMIN privileges.
- **Authentication (BE)**: May vary based on role `/admin/auth/login` or `/auth/login`.
- **Authentication (FE)**: May vary based on role `/admin/login` or `/login`.
- **Good to know**: Since http cookie is declared as secure, a https connection is required to access the application.
  See how cookies are managed at `AuthService.setSessionCookies` on the frontend.

*Note: Security is managed via PASETO tokens, which offer a more modern and secure alternative to traditional JWTs.*

---

## 🧪 Testing

### Backend
Run the full suite of unit and integr[opencollection.yml](../../Documents/bruno/ME/opencollection.yml)ation tests:
```bash
./gradlew :backend:test
```
*JaCoCo coverage reports are automatically generated after the tests.*

### Frontend
- **Unit Tests**: `cd frontend && pnpm test`
- **E2E Tests**: `cd frontend && pnpm test:e2e`

---

## 🐕 API Collections (Bruno)

The project includes a [Bruno](https://www.usebruno.com/) collection for testing the API endpoints.

### How to use:
1.  Install **Bruno** from the official website.
2.  Open Bruno and select **"Open Collection"**.
3.  Browse to the `bruno` folder in the root of this project.
4.  The collection includes:
    -   **Admin**: Login and Registration for administrative tasks.
    -   **User**: Complete CRUD operations for Person management (Get, Search, Add, Update, Delete).

Make sure the backend is running before executing requests.

---

## 🐳 Dockerization

Each module has its own optimized `Dockerfile` (multi-stage builds) for production:
- **Backend**: Lightweight image based on JRE 25 with a non-root user.
- **Frontend**: Uses Next.js `standalone` mode to minimize image size.

To stop all services:
```bash
docker compose down
```
