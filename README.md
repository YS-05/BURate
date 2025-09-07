# BU Rate 🎓

A comprehensive course review and planning platform built specifically for Boston University students.

**Live Application**: [burate.org](https://burate.org)

![BU Rate Landing Page](https://github.com/user-attachments/assets/a9a35f3a-d981-4d57-af37-3f6de26b1b0d)

## Overview

BU Rate addresses the lack of detailed course information available to BU students by providing a centralized platform for course reviews, advanced search capabilities, and academic planning tools. Built with modern web technologies, it serves the entire BU student body of 30,000+ students with data on 7,500+ courses.

## Features

### 📝 Course Reviews

- **5-category rating system**: Usefulness, difficulty, workload, interest, and teacher
- **Course-specific teacher ratings**: See how professors perform in specific courses, not just overall
- **Teacher performance analysis**: Sort reviews by teacher to get average teacher scores for specific courses
- **Detailed student feedback**: Get the real insights you need for course selection

### 🔍 Advanced Search & Filtering

- Filter by prerequisites (courses with/without requirements)
- Search by rating categories and thresholds
- Standard academic filters (college, department, course code)
- HUB requirement filtering
- Combine multiple filters for precise results

### 📊 User Dashboard & HUB Progress Tracking

- **Personal dashboard** for registered users with academic planning tools
- **Course management**: Add courses to completed or future course lists
- **Dynamic HUB tracking**: Real-time updates to HUB progress based on your course selections
- **Visual progress graphs** for all HUB categories
- Plan your academic path efficiently with semester-by-semester tracking

## Tech Stack

### Backend

- **Spring Boot** (Java) - REST API and business logic
- **PostgreSQL** - Primary database for courses, reviews, and user data
- **JWT Authentication** - Secure user authentication and authorization
- **JSoup** - Web scraping for course data collection
- **JUnit** - Comprehensive testing framework

### Frontend

- **React** - Component-based user interface
- **TypeScript** - Type-safe development
- **Responsive Design** - Optimized for desktop and mobile

### Infrastructure

- **Deployed Application** - Production-ready deployment
- **RESTful APIs** - Clean, documented API endpoints
- **Database Migration Scripts** - Automated data management

## Key Technical Achievements

- **Data Collection**: Automated web scraping system that collects and structures course information from multiple university sources
- **Scalable Architecture**: Designed to handle the entire BU student population with efficient database queries and caching
- **Security**: Implemented JWT-based authentication with secure password handling
- **Testing**: Extensive test coverage ensuring application reliability
- **User Experience**: Intuitive interface design focused on student workflow and course planning needs

## Installation & Setup

### Prerequisites

- Java 17+
- Node.js 16+
- PostgreSQL 12+

### Backend Setup

```bash
# Clone the repository
git clone https://github.com/YS-05/BURate.git
cd BURate/backend

# Configure database connection in application.properties
# Run the application
./mvnw spring-boot:run
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

## API Documentation

### Authentication

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Courses

- `GET /api/courses` - Get courses with filtering
- `GET /api/courses/{id}` - Get specific course details
- `GET /api/courses/search` - Advanced course search

### Reviews

- `POST /api/reviews` - Submit course review
- `GET /api/reviews/course/{courseId}` - Get reviews for a course
- `PUT /api/reviews/{id}` - Update review

### HUB Tracking

- `GET /api/hub/progress` - Get user's HUB progress
- `POST /api/hub/courses` - Add course to planning

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

- [ ] Mobile app development
- [ ] Professor rating integration
- [ ] Course scheduling optimization
- [ ] Email notifications for course updates
- [ ] Integration with StudentLink
- [ ] Advanced analytics dashboard

## Contact

**Yash Sharma** - [ysharma@bu.edu](mailto:ysharma@bu.edu) - [LinkedIn] [https://www.linkedin.com/in/yash-sharma-ys05/]

**Project Link**: [https://github.com/YS-05/BURate](https://github.com/YS-05/BURate)

**Live Application**: [burate.org](https://burate.org)

---
