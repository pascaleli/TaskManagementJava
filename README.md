# Task Management System Documentation

## Overview
The Task Management System is a secure, containerized application that provides REST API endpoints for managing tasks with user authentication and authorization. The system is built using Spring Boot, secured with JWT authentication, and runs with MySQL in Docker.
This project is hooked to a cicd pipeline on Jenkins, to execute tests and generate reports periodically or could be triggered base on push to repo on Manual action. 

[Watch Video](https://drive.google.com/file/d/1mKLU6_fFfotoPBl_A86ShJQIGNVygdXp/view?usp=sharing)

## System Architecture

### Components
- Backend: Spring Boot application
- Database: MySQL (containerized)
- CI/CD: Jenkins Pipeline (containerized)
- Authentication: JWT-based security

### Technical Stack
- Java with Spring Boot
- Maven for build management
- Docker for containerization
- Jenkins for CI/CD
- MySQL for data persistence

## Development Environment Setup

### Prerequisites
- Install Docker and Docker Compose
- Java Development Kit (JDK) 17
- Maven 
- Git
  
These are prerequisites, however if you already have mysql configured on your locals , you do not need docker . you can just use the application.property to configure the details to connect to your db

### Configuration Files
1. `docker-compose.yml`: Configures MySQL database container
2. `application.properties`: Contains database connection and application settings
3. `Jenkinsfile`: Defines CI/CD pipeline configuration

## CI/CD Pipeline

The project uses Jenkins for continuous integration and deployment, configured to:
- Run every 2 minutes (configured via cron)
- Trigger on repository pushes
- Execute in a Maven with OpenJDK 17 environment

### Pipeline Stages
1. **Checkout**: Clones the repository from GitHub
2. **Build**: Executes Maven clean package
3. **Test**: Runs unit tests and generates reports
4. **Post-Actions**: Publishes test results and sends notifications


## SETUP 

1. Clone the repo
2. If using Docker , start you docker app
3. run docker compose up -d    // This will pull mysql image and do all the setup for you based on the default config in docker-compose.yml
4. If using an already existing Msql , Kindly configure the details of you db in application.properties
5. Install pom dependencies
6. Run the application mvn spring-boot:run
 
## API Documentation


### Authentication Endpoints

<img width="1108" alt="image" src="https://github.com/user-attachments/assets/da0c84db-575c-4212-93b6-d1a7e471d497" />

#### User Registration
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
    "username": "username",
    "password": "password"
}
```

#### User Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "username",
    "password": "password"
}
```
- Returns: JWT token for authentication  like this  <eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb2xpZCB0ZXN0IiwiaWF0IjoxNzM5MzQ2NDI2LCJleHAiOjE3Mzk0MzI4MjZ9.OUKlPnhar2KDp5scGHKCARzM5psBsY3lRK0anRdaB6fGFmKQfHp4tpiEXSHEHaeoMghE1nvr6ewHuJAqx-aDag>

### Task Management Endpoints

#### Create Task
```http
POST http://localhost:8080/api/tasks
Authorization:Bearer
TOKEN : jwt-token
PREFIX: Bearer
Content-Type: application/json

{
    "title": "Task Title",
    "description": "Task description",
    "priority": "LOW",
    "status": "IN_PROGRESS",
    "deadline": "2025-03-15T18:00:00"
}
```

#### Filter Tasks by Status
```http
GET http://localhost:8080/api/tasks/user/{userId}/status/{status}
Authorization: Bearer
Authorization:Bearer
TOKEN : jwt-token
PREFIX: Bearer
```

#### Filter Tasks by Priority
```http
GET http://localhost:8080/api/tasks/user/{userId}/priority/{priority}
Authorization: Bearer {jwt-token}
```

### Data Models

#### Task Priority Enum
- LOW
- MEDIUM
- HIGH

#### Task Status Enum
- TODO
- IN_PROGRESS
- COMPLETED

## Security Features

### Authentication
- JWT-based authentication
- Token required for all task-related operations
- Secure password handling

### Authorization
- Users can only access their own tasks
- Automatic user ID extraction from JWT token
- Request validation for task ownership

## Business Rules

1. User Registration and Authentication:
   - Open registration for all users
   - Credentials required for JWT token generation
   - Token required for all task operations

2. Task Management:
   - Users can only create tasks for themselves
   - Tasks must use predefined status and priority enums
   - Task ownership is enforced through JWT validation
   - Users can only view and modify their own tasks

3. Data Validation:
   - Priority must match predefined enum values
   - Status must match predefined enum values
   - Deadline dates must be valid



## Deployment

If you have you have a local DB Msql , you can connect it in the application property file 
Else install docker and the application will automatically spin up a docker image 
Make sure the following ports(3306:3306) and(8080) are free for docker to use for Msql and Jenkins 

The application is by default designed to run in a containerized environment:
1. Database runs in Docker container
2. Application connects via properties configuration
3. Jenkins pipeline handles build and test processes
4. All components are containerized for consistency



## Testing

The system includes automated tests that:
- Verify API functionality
- Test logic
- Ensure data integrity

Test results are automatically published through Jenkins after each build.
http://localhost:8080/job/TaskManagementSystem/lastBuild/testReport/


