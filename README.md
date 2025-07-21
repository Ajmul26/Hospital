# Spring Boot CRUD Application with H2 Database

A comprehensive Spring Boot application demonstrating CRUD (Create, Read, Update, Delete) operations using H2 in-memory database.

## Features

- ✅ Complete CRUD operations for User management
- ✅ H2 in-memory database integration
- ✅ RESTful API endpoints
- ✅ Input validation using Bean Validation
- ✅ Global exception handling
- ✅ Sample data initialization
- ✅ Comprehensive test coverage
- ✅ H2 Console for database inspection

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **JUnit 5**

## Project Structure

```
src/
├── main/
│   ├── java/com/example/springcrudh2/
│   │   ├── SpringCrudH2Application.java     # Main application class
│   │   ├── entity/
│   │   │   └── User.java                    # User entity
│   │   ├── dto/
│   │   │   ├── UserRequest.java             # Request DTO
│   │   │   └── UserResponse.java            # Response DTO
│   │   ├── repository/
│   │   │   └── UserRepository.java          # JPA repository
│   │   ├── service/
│   │   │   └── UserService.java             # Business logic
│   │   ├── controller/
│   │   │   ├── UserController.java          # REST controller
│   │   │   └── GlobalExceptionHandler.java  # Exception handling
│   │   └── config/
│   │       └── DataInitializer.java         # Sample data setup
│   └── resources/
│       └── application.properties           # Configuration
└── test/
    └── java/com/example/springcrudh2/
        └── SpringCrudH2ApplicationTests.java # Unit tests
```

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create a new user |
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `PUT` | `/api/users/{id}` | Update user by ID |
| `DELETE` | `/api/users/{id}` | Delete user by ID |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `GET` | `/api/users/search?term={term}` | Search users by name or email |
| `GET` | `/api/users/name/{name}` | Get users by name containing |
| `GET` | `/api/users/exists/email/{email}` | Check if email exists |
| `GET` | `/api/users/count` | Get total user count |
| `GET` | `/api/users/health` | Health check endpoint |

## Request/Response Examples

### Create User
```bash
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890"
}
```

### Response
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "createdAt": "2023-12-01T10:30:00",
  "updatedAt": "2023-12-01T10:30:00"
}
```

### Update User
```bash
PUT /api/users/1
Content-Type: application/json

{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "phone": "+0987654321"
}
```

### Search Users
```bash
GET /api/users/search?term=john
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd spring-crud-h2
```

2. **Build the application**
```bash
mvn clean compile
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- API Base URL: `http://localhost:8080/api/users`
- H2 Console: `http://localhost:8080/h2-console`

### H2 Database Console

Access the H2 console at `http://localhost:8080/h2-console`

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### Running Tests

```bash
mvn test
```

## Sample Data

The application automatically initializes with sample users:
- John Doe (john.doe@example.com)
- Jane Smith (jane.smith@example.com)
- Bob Johnson (bob.johnson@example.com)
- Alice Brown (alice.brown@example.com)
- Charlie Wilson (charlie.wilson@example.com)

## Configuration

### Database Configuration (`application.properties`)
```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Validation Rules

- **Name**: Required, 2-50 characters
- **Email**: Required, valid email format, unique
- **Phone**: Optional, max 15 characters

## Error Handling

The application includes comprehensive error handling:
- Validation errors return detailed field-level messages
- Business logic errors return appropriate HTTP status codes
- Global exception handler for consistent error responses

## Testing with cURL

### Create a user
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","phone":"+1234567890"}'
```

### Get all users
```bash
curl http://localhost:8080/api/users
```

### Get user by ID
```bash
curl http://localhost:8080/api/users/1
```

### Update user
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated User","email":"updated@example.com","phone":"+0987654321"}'
```

### Delete user
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### Search users
```bash
curl "http://localhost:8080/api/users/search?term=john"
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
