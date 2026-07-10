LearnBridge is a role-based Learning Management System (LMS) built for students and teachers. The backend powers course management, quizzes, coding practice, real-time live quizzes, and student progress tracking.

🔗 **Live Demo:** [learning-platform-frontend-delta.vercel.app](https://learning-platform-frontend-delta.vercel.app)
🔗 **Frontend Repo:** [learning-platform-frontend](https://github.com/Aftab-sh/learning-platform-frontend)

---

## Features

### Authentication & Authorization
- JWT-based Authentication
- Spring Security Integration
- Role-Based Access Control (Student & Teacher)
- Password Encryption using BCrypt

### Course Management
- Create and manage courses
- Module management
- Structured, sequential learning path

### Quiz System
- Create quizzes and questions
- Automatic evaluation
- Score tracking

### Coding Practice
- Coding problem management
- Judge0 API integration for code execution
- Instant code evaluation with multi-language support

### Live Quiz
- Real-time quiz system using WebSocket
- Live room creation
- Multiple students can participate simultaneously

### Progress Tracking
- Student learning progress
- Quiz performance tracking
- Coding activity monitoring

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security, JWT |
| Data Layer | Spring Data JPA, Hibernate |
| Database | MySQL |
| Real-time | WebSocket (STOMP) |
| Code Execution | Judge0 API |
| Build Tool | Maven |
| Deployment | Docker |

---

## Project Structure

```
src/
├── controller/    # REST API endpoints
├── service/       # Business logic
├── repository/    # Database access layer
├── entity/        # JPA entities
├── dto/           # Data transfer objects
├── config/        # App and security configuration
├── security/      # JWT filter and auth logic
└── exception/     # Global exception handling
```

---

## API Modules

- Authentication APIs
- Course APIs
- Module APIs
- Quiz APIs
- Coding APIs
- Progress APIs
- Live Quiz APIs

---

## Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/Aftab-sh/learning-platform-backend.git
cd learning-platform-backend

# 2. Configure your MySQL database in application.properties
#    (or use environment variables for DB URL, username, password, JWT secret)

# 3. Run the project
mvn spring-boot:run
```

---

## Future Improvements

- Certificate generation on course completion
- Video lecture support
- Leaderboards
- AI-based course/problem recommendations

---

## Author

Shaikh Aftab
