# BU Rate

**BU Rate** is a full-stack course discovery and review platform built for Boston University students.  
It helps students explore courses, read anonymous reviews, track HUB progress, and get AI-assisted course advice — all in one place.

The platform is designed to be secure, scalable, and data-driven, with a modern React frontend and a Spring Boot backend.

---

## Features

### Course Discovery & Search

- Browse **7,500+ BU courses** with pagination and sorting
- Advanced filtering by:
  - College
  - Department
  - HUB requirements
  - Minimum rating
  - Prerequisites
- Keyword search by course code or name
- Detailed course pages with ratings and reviews

### Anonymous Reviews & Voting

- Create, edit, and delete **anonymous course reviews**
- Filter reviews by instructor
- View instructor-specific scores
- Upvote and downvote reviews to surface helpful feedback
- Personal “My Reviews” section for users

### User Accounts & Security

- Email-verified user registration
- JWT-based authentication and authorization
- Secure password reset and update flows
- Account settings management
- Full account deletion support

### HUB Progress Tracking

- Mark courses as completed
- Automatic HUB requirement progress calculation
- Personalized dashboard with academic overview

### AI Course Advisor

- Chat-based advisor trained on:
  - BU course data
  - Student reviews
- Ask natural questions about:
  - Course difficulty
  - Workload
  - Comparisons between classes
  - Academic fit
- Retrieval-Augmented Generation (RAG) pipeline using vector embeddings

---

## Tech Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL + pgvector
- LangChain4j (RAG)
- JSoup (course ingestion)
- JUnit + H2 (testing)
- Resend (email delivery)

### Frontend

- React
- TypeScript
- Vite
- Bootstrap
- Custom CSS

---

## API Overview

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/verify`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `PUT  /api/auth/update-password`
- `GET  /api/auth/me`

### Courses

- `GET /api/courses`
- `GET /api/courses/{id}`
- `GET /api/courses/search`
- `GET /api/courses/colleges`
- `GET /api/courses/departments/{college}`

### Reviews

- `POST   /api/reviews/course/{courseId}`
- `PUT    /api/reviews/{reviewId}`
- `DELETE /api/reviews/{reviewId}`
- `GET    /api/reviews/course/{courseId}`
- `GET    /api/reviews/my-reviews`

### Votes

- `POST /api/votes/review/{reviewId}`
- `GET  /api/votes/review/{reviewId}`

### Users

- `GET    /api/users/dashboard`
- `GET    /api/users/hub-progress`
- `POST   /api/users/completed-courses/{courseId}`
- `DELETE /api/users/completed-courses/{courseId}`
- `PUT    /api/users/account`
- `DELETE /api/users/delete`

### AI Advisor

- `POST /api/ai/chat`
- `POST /api/ai/sync-courses`
- `POST /api/ai/sync-reviews`

---

## AI Advisor (RAG)

The AI Advisor uses a Retrieval-Augmented Generation (RAG) pipeline:

- Courses and reviews are embedded using MiniLM
- Embeddings are stored in PostgreSQL via pgvector
- Relevant context is retrieved at query time
- Responses are generated using LLMs (OpenAI / Gemini)

This ensures responses are grounded in real BU course and review data.

---

## Local Development

### Backend

```bash
./gradlew bootRun
```

### Frontend

```bash
npm install
npm run dev
```

### Environment Variables (Backend)

```env
POSTGRES_USER=
POSTGRES_PASSWORD=
JDBC_DATABASE_URL=
JWT_SECRET_KEY=
RESEND_API_KEY=
GOOGLE_API_KEY=
```

---

## Testing

- JUnit and Spring Boot Test
- H2 in-memory database for isolated tests
- Spring Security test utilities

Run tests:

```bash
./gradlew test
```

---

## Disclaimer

BU Rate is an independent project and is **not officially affiliated with Boston University**.
