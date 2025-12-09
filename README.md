# Facility Feedback & Helpdesk Request System

A complete Spring Boot 3.5.6 application for managing facility feedback and helpdesk requests, designed with a code-first approach and academic clarity.

## Tech Stack

- **Spring Boot**: 3.5.6
- **Java**: 21
- **Build Tool**: Maven
- **Database**: Microsoft SQL Server (port 1433)
- **Template Engine**: Thymeleaf
- **Security**: Spring Security (session-based)
- **ORM**: Spring Data JPA + Hibernate
- **Utilities**: Lombok, Spring Boot Validation, Spring Boot DevTools

## Project Structure

```
com.example.helpdesk
├── entity          # JPA entities (User, Student, Staff, Department, Room, FeedbackCategory, Ticket)
├── repository      # Spring Data JPA repositories
├── service         # Business logic layer (interfaces + implementations)
├── controller      # MVC controllers
├── config          # Configuration classes (Security, Data Initialization)
└── util            # Utility classes (if needed)
```

## Prerequisites

1. **Java 21** - Ensure JDK 21 is installed
2. **Maven 3.6+** - For building the project
3. **Microsoft SQL Server** - Database server running on port 1433
4. **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## Database Setup

### 1. Install SQL Server

Ensure Microsoft SQL Server is installed and running on port 1433.

### 2. Create Database

Connect to SQL Server and create the database:

```sql
CREATE DATABASE facility_feedback_helpdesk_request_system_db;
```

### 3. Configure Database Connection

The application is pre-configured in `application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=facility_feedback_helpdesk_request_system_db
spring.datasource.username=sa
spring.datasource.password=YourStrong!Passw0rd
```

**Important**: Update the username and password in `src/main/resources/application.properties` to match your SQL Server credentials.

### 4. Database Schema

The database schema is automatically created by Hibernate using `ddl-auto=update`. Tables will be created on first run:

- `users` - User accounts
- `students` - Student information
- `staff` - Staff information
- `departments` - Department information
- `rooms` - Room information
- `feedback_categories` - Ticket categories
- `tickets` - Helpdesk tickets

## Running the Application

### 1. Clone or Extract Project

Navigate to the project directory:

```bash
cd FacilityFeedbackAndHelpdeskRequestSystem
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the main class `com.example.helpdesk.HelpdeskApplication` from your IDE.

### 4. Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

You will be redirected to the login page.

## Default Credentials

The application automatically creates a demo user on first startup:

- **Email**: `demo@fpt.edu.vn`
- **Password**: `demo123`

## Features Implemented

### Submit Ticket Feature

- **GET `/tickets/new`** - Display ticket submission form
- **POST `/tickets`** - Process and save ticket submission

#### Business Logic

- Automatic status assignment: `CREATED`
- Automatic timestamp assignment: Current date/time
- Default priority: `MEDIUM` (if not specified)
- Validation: Subject, description, and creator are required
- Transactional: All operations are wrapped in `@Transactional` for data consistency

## Architecture

### Layered Architecture

1. **Entity Layer** - JPA entities representing database tables
2. **Repository Layer** - Data access using Spring Data JPA
3. **Service Layer** - Business logic (interfaces + implementations)
4. **Controller Layer** - HTTP request handling (thin controllers)

### Design Principles

- **Code-First Approach** - Entities define the database schema
- **No DTOs** - Thymeleaf forms bind directly to JPA entities
- **Separation of Concerns** - Business logic in services, not controllers
- **Transaction Management** - `@Transactional` ensures data consistency

## UI Design

The application uses a custom theme inspired by FPT University:

- **Primary Color**: Orange (#F37021)
- **Background**: White
- **Text**: Dark gray (#333)
- **Clean Layout**: Simple, modern design without external frameworks

## Testing

### Run Unit Tests

```bash
mvn test
```

### Test Coverage

- `TicketServiceTest` - Tests ticket creation with various scenarios:
  - Default status assignment
  - Automatic timestamp assignment
  - Validation rules
  - Error handling

## Configuration Files

### application.properties

Located at `src/main/resources/application.properties`:

- Database connection settings
- JPA/Hibernate configuration
- Thymeleaf settings
- Logging configuration

### pom.xml

Maven configuration with all required dependencies:
- Spring Boot starters
- SQL Server JDBC driver
- Lombok
- Testing dependencies (JUnit 5, Mockito)

## Development

### Hot Reload

Spring Boot DevTools is included for automatic application restart during development.

### Logging

Logging is configured to show:
- DEBUG level for `com.example.helpdesk` package
- DEBUG level for Spring Security

Check console output for detailed logs.

## Troubleshooting

### Database Connection Issues

1. Verify SQL Server is running on port 1433
2. Check database credentials in `application.properties`
3. Ensure the database exists
4. Check firewall settings

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:

```properties
server.port=8081
```

### Build Errors

1. Ensure Java 21 is installed and configured
2. Run `mvn clean install` to rebuild
3. Check Maven settings.xml for proxy configuration if needed

## Next Steps

To extend the application:

1. **Ticket List View** - Implement the ticket list functionality
2. **Ticket Detail View** - Add detailed ticket viewing
3. **User Management** - Add user registration and management
4. **Ticket Assignment** - Assign tickets to staff members
5. **Status Updates** - Allow status changes and updates
6. **Email Notifications** - Send email notifications for ticket updates

## License

This project is created for educational purposes.

## Author

Facility Helpdesk Team

---

For questions or issues, please refer to the code documentation or contact the development team.



