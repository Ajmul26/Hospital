# Spring Boot CRUD API Examples

This document provides examples of how to use all the CRUD endpoints in the Spring Boot H2 application.

## Base URL
```
http://localhost:8080/api/users
```

## 1. Get All Users
**GET** `/api/users`

```bash
curl -X GET http://localhost:8080/api/users
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "createdAt": "2025-07-21T11:57:30.857454",
    "updatedAt": "2025-07-21T11:57:30.857477"
  }
]
```

## 2. Get User by ID
**GET** `/api/users/{id}`

```bash
curl -X GET http://localhost:8080/api/users/1
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "createdAt": "2025-07-21T11:57:30.857454",
  "updatedAt": "2025-07-21T11:57:30.857477"
}
```

## 3. Create New User
**POST** `/api/users`

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phone": "+1111111111"
  }'
```

**Response:**
```json
{
  "id": 6,
  "name": "Test User",
  "email": "test@example.com",
  "phone": "+1111111111",
  "createdAt": "2025-07-21T11:57:45.834097",
  "updatedAt": "2025-07-21T11:57:45.834113"
}
```

## 4. Update User
**PUT** `/api/users/{id}`

```bash
curl -X PUT http://localhost:8080/api/users/6 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test User",
    "email": "updated.test@example.com",
    "phone": "+2222222222"
  }'
```

**Response:**
```json
{
  "id": 6,
  "name": "Updated Test User",
  "email": "updated.test@example.com",
  "phone": "+2222222222",
  "createdAt": "2025-07-21T11:57:45.834097",
  "updatedAt": "2025-07-21T11:58:01.123456"
}
```

## 5. Delete User
**DELETE** `/api/users/{id}`

```bash
curl -X DELETE http://localhost:8080/api/users/6
```

**Response:**
```json
{
  "message": "User deleted successfully"
}
```

## 6. Search Users
**GET** `/api/users/search?term={searchTerm}`

```bash
curl -X GET "http://localhost:8080/api/users/search?term=John"
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "createdAt": "2025-07-21T11:57:30.857454",
    "updatedAt": "2025-07-21T11:57:30.857477"
  },
  {
    "id": 3,
    "name": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1122334455",
    "createdAt": "2025-07-21T11:57:30.907674",
    "updatedAt": "2025-07-21T11:57:30.907681"
  }
]
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-07-21T11:58:00.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users",
  "errors": {
    "name": "Name is required",
    "email": "Email should be valid"
  }
}
```

### User Not Found (404 Not Found)
```json
{
  "timestamp": "2025-07-21T11:58:00.123456",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999"
}
```

### Email Already Exists (409 Conflict)
```json
{
  "timestamp": "2025-07-21T11:58:00.123456",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: john.doe@example.com",
  "path": "/api/users"
}
```

## H2 Database Console

Access the H2 database console at: `http://localhost:8080/h2-console`

**Connection Settings:**
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: `password`

## Sample Data

The application comes with pre-loaded sample data:
1. John Doe - john.doe@example.com
2. Jane Smith - jane.smith@example.com
3. Bob Johnson - bob.johnson@example.com
4. Alice Brown - alice.brown@example.com
5. Charlie Wilson - charlie.wilson@example.com