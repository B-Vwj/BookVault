# BookVault

A privacy-first personal book library web application. 
Search for books, track your reading status, and manage your collection — no account required.

## Overview

BookVault uses an anonymous JWT session model: on your first visit, a unique session is automatically generated and stored in your browser. 
All library data is scoped to that session, giving you a fully functional personal library without requiring an email address or password.

## Tech Stack

| Layer            | Technology                           |
|------------------|--------------------------------------|
| Frontend         | React (TypeScript), Create React App |
| Backend          | Kotlin, Ktor 3.4.3 (Netty engine)    |
| Database         | PostgreSQL via Supabase              |
| ORM              | Exposed 1.2.0                        |
| Auth             | Anonymous JWT sessions (java-jwt)    |
| Book Metadata    | Open Library API                     |
| Frontend Hosting | Vercel                               |
| Backend Hosting  | Render                               |

## Live Demo

- **BookVault UI:** https://temporary-racing-zinc-s7x14qf.vercel.app/

> **Note:** The backend is hosted on Render's free tier and may take 30–60 seconds to wake up after a period of inactivity.

## Features

- Search books by title or ISBN via the Open Library API
- Automatically retrieve cover art, author, publication year, page count, and edition
- Add books to a personal library scoped to your browser session
- Track reading status: Want to Read, Currently Reading, or Finished
- Rate books with a 1–5-star rating
- Remove books from your library
- Fully responsive UI for desktop and mobile

## Project Structure

```
BookVault/
├── src/                          # Ktor backend
│   ├── main/
│   │   ├── kotlin/com/bookvault/
│   │   │   ├── db/               # Exposed table definitions & DatabaseFactory
│   │   │   ├── models/           # Data classes (Book, Session)
│   │   │   ├── plugins/          # Ktor plugin configuration
│   │   │   ├── routes/           # API route handlers
│   │   │   ├── services/         # Business logic (BookService, SessionService, OpenLibraryService)
│   │   │   └── Main.kt           # Application entry point
│   │   └── resources/
│   │       └── application.conf  # HOCON configuration
│   └── test/                     # Integration tests
├── frontend/                     # React frontend
│   ├── src/
│   │   ├── api/                  # API client
│   │   ├── components/           # React components
│   │   ├── hooks/                # Custom hooks (useSession, useBooks)
│   │   └── types/                # TypeScript type definitions
│   └── public/
├── docker-compose.yml            # Local PostgreSQL via Docker
├── Dockerfile                    # Production Docker image for Render
└── README.md
```

## API Endpoints

| Method | Endpoint              | Auth | Description                           |
|--------|-----------------------|------|---------------------------------------|
| GET    | `/health`             | None | Health check                          |
| POST   | `/session`            | None | Create anonymous session, returns JWT |
| GET    | `/search?q={query}`   | None | Search books by title                 |
| GET    | `/search?isbn={isbn}` | None | Search books by ISBN                  |
| GET    | `/books`              | JWT  | Get all books in session library      |
| POST   | `/books`              | JWT  | Add a book to the library             |
| PUT    | `/books/{id}`         | JWT  | Update book status or rating          |
| DELETE | `/books/{id}`         | JWT  | Remove a book from the library        |

## Running Locally

### Prerequisites

- JDK 21
- Node.js 18+
- Docker (with Colima on macOS, or Docker Desktop)

### 1. Clone the repository

```bash
git clone https://github.com/b-vwj/bookvault.git
cd bookvault
```

### 2. Start the local database

```bash
colima start
docker compose up -d
```

### 3. Configure the backend environment

Create a `.env` file at the repo root:

```env
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=local-dev-secret
JWT_ISSUER=bookvault
JWT_AUDIENCE=bookvault-users
```

### 4. Run the database schema

Connect to the local database and run the schema:

```bash
docker exec -it bookvault-db psql -U postgres
```

Then paste and execute:

```SQL
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    open_library_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    cover_url VARCHAR(512),
    publication_year INT,
    edition VARCHAR(255),
    page_count INT,
    status VARCHAR(50) NOT NULL DEFAULT 'WANT_TO_READ',
    rating INT CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_books_session_id ON books(session_id);
```

### 5. Start the backend

In IntelliJ IDEA, open the `bookvault` project and run `Main.kt`, or from the terminal:

```bash
./gradlew run
```

The backend will start at `http://localhost:8080`.

### 6. Start the frontend

```bash
cd frontend
npm install
npm start
```

The frontend will start at `http://localhost:3000`.

## Running Tests

```bash
./gradlew test
```

Tests require the local Docker database to be running.

## Environment Variables

| Variable       | Description                 | Example                                     |
|----------------|-----------------------------|---------------------------------------------|
| `DB_URL`       | JDBC connection URL         | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USER`      | Database username           | `postgres`                                  |
| `DB_PASSWORD`  | Database password           | `postgres`                                  |
| `JWT_SECRET`   | Secret key for signing JWTs | `your-secret-key`                           |
| `JWT_ISSUER`   | JWT issuer claim            | `bookvault`                                 |
| `JWT_AUDIENCE` | JWT audience claim          | `bookvault-users`                           |

## Architecture Notes

**Anonymous Sessions:** No user accounts or personally identifiable information are collected. On first visit, the frontend calls `POST /session`, receives a JWT, and stores it in `localStorage`. All subsequent API calls include this token in the `Authorization` header, scoping all database queries to the session UUID.

**Open Library API:** Book metadata is fetched from the [Open Library API](https://openlibrary.org/developers/api) at the backend layer. The frontend never contacts Open Library directly. Responses are intentionally kept low-volume and human-facing, in alignment with Open Library's usage guidelines.

**Connection Pooling:** HikariCP manages the PostgreSQL connection pool. Supabase's Session Pooler connection method is required (not Transaction Pooler) because Exposed uses prepared statements.

## License

MIT License — see [LICENSE](LICENSE) for details.