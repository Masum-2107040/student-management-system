# Student Management System

A Spring Boot application for managing students, teachers, departments, and courses with role-based access control.

## Technologies Used
- Spring Boot 3.2.2
- PostgreSQL 15
- Docker
- Spring Security
- Thymeleaf
- Maven

## Prerequisites
- Java 17 or higher (✓ You have Java 23)
- Docker Desktop
- IntelliJ IDEA

## Quick Start with IntelliJ IDEA

### 1. Open Project
- Open IntelliJ IDEA
- File → Open → Select this folder
- Wait for dependencies to download

### 2. Start PostgreSQL Database
Open PowerShell in this directory and run:
```powershell
docker-compose up -d
```

### 3. Run the Application
- In IntelliJ, navigate to: `src/main/java/com/sms/StudentManagementSystemApplication.java`
- Right-click → Run 'StudentManagementSystemApplication'
- Wait for startup (look for "Started StudentManagementSystemApplication" in console)

### 4. Access the Application
Open your browser: `http://localhost:8080`

## Default Users
- **Admin/Teacher**: username: `teacher@example.com`, password: `teacher123`
- **Student**: username: `student@example.com`, password: `student123`

## Features
- User authentication and authorization
- Role-based access control (STUDENT, TEACHER)
- CRUD operations for Students, Teachers, Departments, and Courses
- Students cannot modify teacher profiles
- Teachers can manage both student and teacher profiles

## API Endpoints
- `/login` - Login page
- `/students` - Student management
- `/teachers` - Teacher management
- `/departments` - Department management
- `/courses` - Course management
