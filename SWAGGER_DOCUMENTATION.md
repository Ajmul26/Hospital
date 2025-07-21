# Swagger/OpenAPI 3 Documentation

This document explains how to access and use the Swagger UI documentation for the Spring Boot CRUD API.

## 🚀 Quick Access

Once the application is running, you can access:

- **Swagger UI Interface**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON Documentation**: http://localhost:8080/v3/api-docs

## 📋 What's Included

### ✅ Comprehensive API Documentation
- All CRUD endpoints with detailed descriptions
- Request/Response schemas with examples
- HTTP status codes and error responses
- Parameter descriptions and validation rules

### ✅ Interactive Testing
- Try out API endpoints directly from the browser
- Fill in parameters and request bodies
- Execute real API calls and see responses
- Copy curl commands for command-line testing

### ✅ Schema Documentation
- `UserRequest` schema for creating/updating users
- `UserResponse` schema for API responses
- Field descriptions, examples, and validation constraints

## 📚 API Endpoints Overview

### User Management Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create new user |
| `PUT` | `/api/users/{id}` | Update existing user |
| `DELETE` | `/api/users/{id}` | Delete user |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `GET` | `/api/users/search?term={term}` | Search users by name/email |
| `GET` | `/api/users/name/{name}` | Get users by name pattern |
| `GET` | `/api/users/exists/email/{email}` | Check if email exists |
| `GET` | `/api/users/count` | Get total user count |
| `GET` | `/api/users/health` | Health check endpoint |

## 🔧 Configuration

The Swagger configuration is defined in:

### 1. Dependencies (pom.xml)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 2. Application Properties
```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.try-it-out-enabled=true
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.filter=true
```

### 3. OpenAPI Configuration Class
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .description("Spring Boot CRUD Application with H2 Database")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Spring Boot CRUD API")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")
                ));
    }
}
```

## 📝 Annotation Examples

### Controller Level
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for managing users in the system")
public class UserController {
    // ... controller methods
}
```

### Method Level
```java
@Operation(summary = "Create a new user", description = "Creates a new user in the system")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
})
@PostMapping
public ResponseEntity<?> createUser(
        @Parameter(description = "User data to create", required = true)
        @Valid @RequestBody UserRequest userRequest) {
    // ... method implementation
}
```

### DTO/Schema Level
```java
@Schema(description = "User request data for creating or updating a user")
public class UserRequest {
    
    @Schema(description = "User's full name", example = "John Doe", required = true)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;
    
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    // ... other fields
}
```

## 🎯 Usage Examples

### 1. Creating a User via Swagger UI
1. Navigate to http://localhost:8080/swagger-ui/index.html
2. Find the "POST /api/users" endpoint
3. Click "Try it out"
4. Fill in the request body:
   ```json
   {
     "name": "John Doe",
     "email": "john.doe@example.com",
     "phone": "+1234567890"
   }
   ```
5. Click "Execute"
6. View the response

### 2. Searching Users
1. Find the "GET /api/users/search" endpoint
2. Click "Try it out"
3. Enter a search term (e.g., "john")
4. Click "Execute"
5. View matching users

### 3. Testing Validation
1. Try creating a user with invalid data (e.g., invalid email)
2. See the validation error responses
3. Understand the required field constraints

## 🌟 Benefits

### For Developers
- **Interactive Testing**: Test APIs without writing separate test scripts
- **Documentation**: Always up-to-date API documentation
- **Examples**: Real examples for request/response formats
- **Validation**: See validation rules and constraints

### For API Consumers
- **Self-Documentation**: Understand API without external documentation
- **Try Before Integrate**: Test API behavior before implementation
- **Copy-Paste Ready**: Get curl commands for quick testing
- **Schema Understanding**: Clear data models and field descriptions

### For Teams
- **Collaboration**: Share API specifications easily
- **Standards**: Consistent API documentation format
- **Testing**: Manual testing capabilities for QA teams
- **Integration**: Easy API exploration for frontend developers

## 🚨 Security Note

In production environments, consider:
- Disabling Swagger UI (`springdoc.swagger-ui.enabled=false`)
- Restricting access to documentation endpoints
- Using API keys or authentication for sensitive APIs
- Keeping OpenAPI docs behind authentication

## 📖 Additional Resources

- [SpringDoc OpenAPI 3 Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## 🔗 Related Files

- `src/main/java/com/example/springcrudh2/config/OpenApiConfig.java` - OpenAPI configuration
- `src/main/java/com/example/springcrudh2/controller/UserController.java` - Controller with annotations
- `src/main/java/com/example/springcrudh2/dto/` - DTO classes with schema annotations
- `src/main/resources/application.properties` - Swagger configuration properties
- `pom.xml` - SpringDoc dependency configuration