# API Documentation

## Overview

This document provides comprehensive documentation for all public APIs in the Hospital Enterprise Application. The API follows RESTful conventions and provides endpoints for managing patients, appointments, medical records, staff, and hospital resources.

## Base URL

```
Production: https://api.hospital.com/v1
Development: https://dev-api.hospital.com/v1
Local: http://localhost:8080/api/v1
```

## Authentication

All API endpoints require authentication using JWT tokens.

### Authentication Flow

```http
POST /auth/login
Content-Type: application/json

{
  "username": "doctor@hospital.com",
  "password": "secure_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user_123",
    "name": "Dr. John Smith",
    "role": "doctor",
    "department": "cardiology"
  },
  "expires_in": 3600
}
```

### Using the Token

Include the token in the Authorization header:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Error Handling

All errors follow a consistent format:

```json
{
  "error": {
    "code": "PATIENT_NOT_FOUND",
    "message": "Patient with ID 12345 not found",
    "details": {
      "patient_id": "12345",
      "timestamp": "2024-01-20T10:30:00Z"
    }
  }
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Unprocessable Entity |
| 500 | Internal Server Error |

## Patient Management API

### Get Patient

Retrieve detailed information about a specific patient.

**Endpoint:** `GET /patients/{patient_id}`

**Parameters:**
- `patient_id` (string, required): Unique identifier for the patient

**Response:**
```json
{
  "id": "pat_123456",
  "personal_info": {
    "first_name": "John",
    "last_name": "Doe",
    "date_of_birth": "1985-03-15",
    "gender": "male",
    "contact": {
      "phone": "+1-555-0123",
      "email": "john.doe@email.com",
      "address": {
        "street": "123 Main St",
        "city": "Healthcare City",
        "state": "HC",
        "zip_code": "12345"
      }
    }
  },
  "medical_info": {
    "blood_type": "O+",
    "allergies": ["penicillin", "shellfish"],
    "emergency_contact": {
      "name": "Jane Doe",
      "relationship": "spouse",
      "phone": "+1-555-0124"
    }
  },
  "created_at": "2023-01-15T10:30:00Z",
  "updated_at": "2024-01-20T14:22:00Z"
}
```

**Example Usage:**
```javascript
// JavaScript/Node.js
const response = await fetch('/api/v1/patients/pat_123456', {
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
});
const patient = await response.json();
```

```python
# Python
import requests

headers = {
    'Authorization': f'Bearer {token}',
    'Content-Type': 'application/json'
}
response = requests.get(f'{base_url}/patients/pat_123456', headers=headers)
patient = response.json()
```

### Create Patient

Register a new patient in the system.

**Endpoint:** `POST /patients`

**Request Body:**
```json
{
  "personal_info": {
    "first_name": "Jane",
    "last_name": "Smith",
    "date_of_birth": "1990-07-22",
    "gender": "female",
    "contact": {
      "phone": "+1-555-0199",
      "email": "jane.smith@email.com",
      "address": {
        "street": "456 Oak Ave",
        "city": "Healthcare City",
        "state": "HC",
        "zip_code": "12345"
      }
    }
  },
  "medical_info": {
    "blood_type": "A-",
    "allergies": ["latex"],
    "emergency_contact": {
      "name": "Bob Smith",
      "relationship": "husband",
      "phone": "+1-555-0198"
    }
  }
}
```

**Response (201 Created):**
```json
{
  "id": "pat_789012",
  "message": "Patient created successfully",
  "created_at": "2024-01-20T15:30:00Z"
}
```

### Update Patient

Update patient information.

**Endpoint:** `PUT /patients/{patient_id}`

**Parameters:**
- `patient_id` (string, required): Unique identifier for the patient

**Request Body:** Same structure as create patient, with optional fields

### Delete Patient

Remove a patient from the system (soft delete).

**Endpoint:** `DELETE /patients/{patient_id}`

**Response (200 OK):**
```json
{
  "message": "Patient deleted successfully",
  "deleted_at": "2024-01-20T16:00:00Z"
}
```

### List Patients

Retrieve a paginated list of patients.

**Endpoint:** `GET /patients`

**Query Parameters:**
- `page` (integer, optional): Page number (default: 1)
- `limit` (integer, optional): Items per page (default: 20, max: 100)
- `search` (string, optional): Search by name, ID, or phone
- `department` (string, optional): Filter by department
- `status` (string, optional): Filter by status (active, inactive)

**Response:**
```json
{
  "patients": [...],
  "pagination": {
    "current_page": 1,
    "total_pages": 25,
    "total_items": 500,
    "items_per_page": 20
  }
}
```

## Appointment Management API

### Schedule Appointment

Create a new appointment.

**Endpoint:** `POST /appointments`

**Request Body:**
```json
{
  "patient_id": "pat_123456",
  "doctor_id": "doc_789012",
  "appointment_type": "consultation",
  "scheduled_time": "2024-01-25T10:00:00Z",
  "duration_minutes": 30,
  "department": "cardiology",
  "notes": "Follow-up consultation",
  "priority": "normal"
}
```

**Response (201 Created):**
```json
{
  "id": "apt_345678",
  "confirmation_number": "CONF-2024-001234",
  "status": "scheduled",
  "created_at": "2024-01-20T16:30:00Z"
}
```

### Get Appointment

**Endpoint:** `GET /appointments/{appointment_id}`

### Update Appointment

**Endpoint:** `PUT /appointments/{appointment_id}`

### Cancel Appointment

**Endpoint:** `DELETE /appointments/{appointment_id}`

### List Appointments

**Endpoint:** `GET /appointments`

**Query Parameters:**
- `patient_id` (string, optional): Filter by patient
- `doctor_id` (string, optional): Filter by doctor
- `date_from` (string, optional): Start date filter (ISO 8601)
- `date_to` (string, optional): End date filter (ISO 8601)
- `status` (string, optional): Filter by status
- `department` (string, optional): Filter by department

## Medical Records API

### Create Medical Record

**Endpoint:** `POST /medical-records`

### Get Medical Record

**Endpoint:** `GET /medical-records/{record_id}`

### Update Medical Record

**Endpoint:** `PUT /medical-records/{record_id}`

### List Patient Medical Records

**Endpoint:** `GET /patients/{patient_id}/medical-records`

## Staff Management API

### Get Staff Member

**Endpoint:** `GET /staff/{staff_id}`

### Create Staff Member

**Endpoint:** `POST /staff`

### Update Staff Member

**Endpoint:** `PUT /staff/{staff_id}`

### List Staff

**Endpoint:** `GET /staff`

## Department Management API

### List Departments

**Endpoint:** `GET /departments`

### Get Department Details

**Endpoint:** `GET /departments/{department_id}`

## Real-time Updates

The API supports WebSocket connections for real-time updates:

**WebSocket Endpoint:** `wss://api.hospital.com/v1/ws`

**Authentication:** Send JWT token as first message

**Example Messages:**
```json
{
  "type": "appointment_updated",
  "data": {
    "appointment_id": "apt_345678",
    "status": "completed",
    "updated_at": "2024-01-20T17:00:00Z"
  }
}
```

## Rate Limiting

API endpoints are rate limited:
- 1000 requests per hour for authenticated users
- 100 requests per hour for unauthenticated endpoints

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642684800
```

## SDKs and Client Libraries

### JavaScript/Node.js

```bash
npm install @hospital/api-client
```

```javascript
import { HospitalAPI } from '@hospital/api-client';

const api = new HospitalAPI({
  baseURL: 'https://api.hospital.com/v1',
  token: 'your-jwt-token'
});

// Get patient
const patient = await api.patients.get('pat_123456');

// Create appointment
const appointment = await api.appointments.create({
  patient_id: 'pat_123456',
  doctor_id: 'doc_789012',
  scheduled_time: '2024-01-25T10:00:00Z'
});
```

### Python

```bash
pip install hospital-api-client
```

```python
from hospital_api import HospitalClient

client = HospitalClient(
    base_url='https://api.hospital.com/v1',
    token='your-jwt-token'
)

# Get patient
patient = client.patients.get('pat_123456')

# Create appointment
appointment = client.appointments.create(
    patient_id='pat_123456',
    doctor_id='doc_789012',
    scheduled_time='2024-01-25T10:00:00Z'
)
```

## API Versioning

- Current version: v1
- Version is specified in the URL path
- Backward compatibility is maintained for at least 12 months
- Deprecation notices are provided 6 months in advance

## Support and Contact

- Documentation: https://docs.hospital.com
- Support Email: api-support@hospital.com
- Status Page: https://status.hospital.com
- GitHub Issues: https://github.com/hospital/api/issues