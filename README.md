# Full Stack User Management Application

A comprehensive full-stack application demonstrating modern web development practices with a Spring Boot backend and React frontend, featuring complete CRUD operations, security scanning, and production-ready deployment capabilities.

## Features

### Backend (Spring Boot)
- ✅ Complete CRUD operations for User management
- ✅ H2 in-memory database integration
- ✅ RESTful API endpoints with comprehensive search
- ✅ Input validation using Bean Validation
- ✅ Global exception handling
- ✅ Sample data initialization
- ✅ Comprehensive test coverage
- ✅ H2 Console for database inspection
- ✅ Swagger/OpenAPI 3 documentation with interactive UI
- ✅ Veracode security scanning integration (SAST, SCA, Pipeline Scan)
- ✅ CORS configuration for frontend integration

### Frontend (React)
- ✅ Modern React 18 with TypeScript
- ✅ Material-UI design system with custom theming
- ✅ Responsive dashboard with statistics and quick actions
- ✅ User list with search, sort, and pagination (DataGrid)
- ✅ Form-based user creation and editing with validation
- ✅ Real-time form validation with Yup schema
- ✅ Toast notifications for user feedback
- ✅ Error handling with user-friendly messages
- ✅ Mobile-responsive design with breakpoint system
- ✅ User detail view with contact actions

## Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **JUnit 5**
- **Swagger/OpenAPI 3** (SpringDoc)
- **Veracode** (Security Scanning)
- **OWASP Dependency Check** (Vulnerability Analysis)

### Frontend
- **React 18** with TypeScript
- **Material-UI v5** (MUI)
- **React Router v6** (Routing)
- **React Hook Form** (Form management)
- **Yup** (Schema validation)
- **Axios** (HTTP client)
- **React Toastify** (Notifications)

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
│   │       ├── DataInitializer.java         # Sample data setup
│   │       └── OpenApiConfig.java           # Swagger/OpenAPI configuration
│   ├── scripts/
│   │   └── veracode-scan.sh                 # Security scanning script
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

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.6** or higher  
- **Node.js 16** or higher
- **npm** (comes with Node.js)

### Option 1: Quick Start (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd user-management-fullstack

# Start both backend and frontend with one command
./start-full-stack.sh
```

**That's it!** The script automatically:
- ✅ Checks all prerequisites
- ✅ Builds the Spring Boot backend
- ✅ Installs React dependencies  
- ✅ Starts both services
- ✅ Shows access URLs

### Option 2: Manual Setup

#### Backend (Spring Boot)
```bash
# Build and run backend
mvn clean spring-boot:run
```

#### Frontend (React)
```bash
# Install dependencies and start frontend
cd frontend/user-management
npm install
npm start
```

4. **Access the application**

### Backend URLs
- **API Base URL**: `http://localhost:8080/api/users`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`
- **H2 Console**: `http://localhost:8080/h2-console`

### Frontend Setup

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend/user-management
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start the React development server**:
   ```bash
   npm start
   ```

4. **Access the frontend**:
   - **React App**: `http://localhost:3000`
   - **Dashboard**: `http://localhost:3000/`
   - **User List**: `http://localhost:3000/users`
   - **Add User**: `http://localhost:3000/users/new`

### Security Scanning

Run local security scans using the provided script:

```bash
# Make script executable (first time only)
chmod +x scripts/veracode-scan.sh

# Run OWASP dependency check
./scripts/veracode-scan.sh dependency-check

# Run complete local security workflow
./scripts/veracode-scan.sh full-scan

# Upload for Veracode static analysis (requires credentials)
./scripts/veracode-scan.sh static-scan -v YOUR_VID -k YOUR_VKEY
```

**Security Reports:**
- OWASP Dependency Check: `target/dependency-check-report/`
- Software Bill of Materials: `target/bom.json`

### Swagger/OpenAPI Documentation

Access the interactive API documentation at `http://localhost:8080/swagger-ui/index.html`

**Features:**
- Interactive API testing interface
- Complete endpoint documentation
- Request/response schemas with examples
- Try-it-out functionality for all endpoints
- Automatic curl command generation

For raw OpenAPI JSON specification: `http://localhost:8080/v3/api-docs`

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
