# Function Documentation

## Overview

This document provides comprehensive documentation for all public functions, utilities, and services in the Hospital Enterprise Application backend. Functions are organized by module and include detailed parameter descriptions, return values, and usage examples.

## Authentication & Authorization

### authenticateUser

Authenticates a user with email and password credentials.

#### Signature

```typescript
function authenticateUser(
  email: string,
  password: string
): Promise<AuthResult>
```

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `email` | `string` | Yes | User's email address |
| `password` | `string` | Yes | User's password |

#### Return Value

```typescript
interface AuthResult {
  success: boolean;
  user?: User;
  token?: string;
  expiresIn?: number;
  error?: string;
}
```

#### Example Usage

```typescript
import { authenticateUser } from '@/services/auth';

async function handleLogin(email: string, password: string) {
  try {
    const result = await authenticateUser(email, password);
    
    if (result.success) {
      // Store token and user info
      localStorage.setItem('token', result.token!);
      setUser(result.user!);
    } else {
      // Handle authentication error
      setError(result.error);
    }
  } catch (error) {
    console.error('Login failed:', error);
  }
}
```

#### Throws

- `ValidationError`: When email format is invalid
- `AuthenticationError`: When credentials are incorrect
- `NetworkError`: When authentication service is unavailable

### generateJWT

Generates a JSON Web Token for authenticated users.

#### Signature

```typescript
function generateJWT(
  user: User,
  options?: JWTOptions
): string
```

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `user` | `User` | Yes | User object to encode in token |
| `options` | `JWTOptions` | No | Token generation options |

#### JWTOptions Interface

```typescript
interface JWTOptions {
  expiresIn?: string | number; // Default: '1h'
  issuer?: string;            // Default: 'hospital-api'
  audience?: string;          // Default: 'hospital-app'
}
```

#### Example Usage

```typescript
import { generateJWT } from '@/utils/jwt';

const user = {
  id: 'user_123',
  email: 'doctor@hospital.com',
  role: 'doctor',
  department: 'cardiology'
};

const token = generateJWT(user, { expiresIn: '24h' });
```

### verifyPermissions

Checks if a user has the required permissions for an action.

#### Signature

```typescript
function verifyPermissions(
  user: User,
  resource: string,
  action: string
): boolean
```

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `user` | `User` | Yes | User to check permissions for |
| `resource` | `string` | Yes | Resource being accessed (e.g., 'patients', 'appointments') |
| `action` | `string` | Yes | Action being performed (e.g., 'read', 'write', 'delete') |

#### Example Usage

```typescript
import { verifyPermissions } from '@/utils/permissions';

function PatientController() {
  async function deletePatient(req: Request, res: Response) {
    const { user } = req;
    
    if (!verifyPermissions(user, 'patients', 'delete')) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }
    
    // Proceed with deletion
  }
}
```

## Patient Management

### createPatient

Creates a new patient record in the system.

#### Signature

```typescript
function createPatient(
  patientData: CreatePatientRequest
): Promise<Patient>
```

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientData` | `CreatePatientRequest` | Yes | Patient information object |

#### CreatePatientRequest Interface

```typescript
interface CreatePatientRequest {
  personalInfo: {
    firstName: string;
    lastName: string;
    dateOfBirth: string;    // ISO 8601 date
    gender: 'male' | 'female' | 'other';
    contact: {
      phone: string;
      email?: string;
      address: Address;
    };
  };
  medicalInfo: {
    bloodType?: string;
    allergies?: string[];
    emergencyContact: EmergencyContact;
  };
}
```

#### Example Usage

```typescript
import { createPatient } from '@/services/patients';

async function registerNewPatient(patientData: CreatePatientRequest) {
  try {
    const patient = await createPatient(patientData);
    console.log('Patient created:', patient.id);
    return patient;
  } catch (error) {
    if (error instanceof ValidationError) {
      // Handle validation errors
      console.error('Validation failed:', error.details);
    }
    throw error;
  }
}
```

#### Throws

- `ValidationError`: When patient data is invalid
- `DuplicateError`: When patient already exists
- `DatabaseError`: When database operation fails

### updatePatient

Updates an existing patient's information.

#### Signature

```typescript
function updatePatient(
  patientId: string,
  updates: Partial<UpdatePatientRequest>
): Promise<Patient>
```

#### Example Usage

```typescript
import { updatePatient } from '@/services/patients';

const updates = {
  personalInfo: {
    contact: {
      phone: '+1-555-0199'
    }
  },
  medicalInfo: {
    allergies: ['penicillin', 'shellfish', 'latex']
  }
};

const updatedPatient = await updatePatient('pat_123456', updates);
```

### findPatients

Searches for patients based on various criteria.

#### Signature

```typescript
function findPatients(
  criteria: SearchCriteria,
  options?: SearchOptions
): Promise<SearchResult<Patient>>
```

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `criteria` | `SearchCriteria` | Yes | Search criteria object |
| `options` | `SearchOptions` | No | Pagination and sorting options |

#### SearchCriteria Interface

```typescript
interface SearchCriteria {
  query?: string;           // Text search across name, email, phone
  dateOfBirth?: string;     // Exact date match
  gender?: string;
  bloodType?: string;
  department?: string;
  status?: 'active' | 'inactive';
  dateRange?: {
    from: string;
    to: string;
  };
}
```

#### Example Usage

```typescript
import { findPatients } from '@/services/patients';

const searchCriteria = {
  query: 'John',
  department: 'cardiology',
  status: 'active'
};

const options = {
  page: 1,
  limit: 20,
  sortBy: 'lastName',
  sortOrder: 'asc'
};

const results = await findPatients(searchCriteria, options);

console.log(`Found ${results.total} patients`);
results.data.forEach(patient => {
  console.log(`${patient.firstName} ${patient.lastName}`);
});
```

## Appointment Management

### scheduleAppointment

Creates a new appointment in the system.

#### Signature

```typescript
function scheduleAppointment(
  appointmentData: CreateAppointmentRequest
): Promise<Appointment>
```

#### CreateAppointmentRequest Interface

```typescript
interface CreateAppointmentRequest {
  patientId: string;
  doctorId: string;
  appointmentType: string;
  scheduledTime: string;      // ISO 8601 datetime
  durationMinutes: number;
  department: string;
  notes?: string;
  priority: 'low' | 'normal' | 'high' | 'urgent';
}
```

#### Example Usage

```typescript
import { scheduleAppointment } from '@/services/appointments';

const appointmentData = {
  patientId: 'pat_123456',
  doctorId: 'doc_789012',
  appointmentType: 'consultation',
  scheduledTime: '2024-01-25T10:00:00Z',
  durationMinutes: 30,
  department: 'cardiology',
  notes: 'Follow-up consultation for chest pain',
  priority: 'normal'
};

const appointment = await scheduleAppointment(appointmentData);
console.log('Appointment scheduled:', appointment.confirmationNumber);
```

### findAvailableSlots

Finds available appointment slots for a doctor or department.

#### Signature

```typescript
function findAvailableSlots(
  criteria: AvailabilityRequest
): Promise<TimeSlot[]>
```

#### AvailabilityRequest Interface

```typescript
interface AvailabilityRequest {
  doctorId?: string;
  department?: string;
  date: string;              // ISO 8601 date
  durationMinutes: number;
  preferredTimes?: string[]; // Array of time preferences
}
```

#### Example Usage

```typescript
import { findAvailableSlots } from '@/services/appointments';

const availabilityRequest = {
  department: 'cardiology',
  date: '2024-01-25',
  durationMinutes: 30,
  preferredTimes: ['09:00', '10:00', '11:00']
};

const slots = await findAvailableSlots(availabilityRequest);

slots.forEach(slot => {
  console.log(`Available: ${slot.startTime} - ${slot.endTime}`);
});
```

### cancelAppointment

Cancels an existing appointment.

#### Signature

```typescript
function cancelAppointment(
  appointmentId: string,
  reason?: string
): Promise<void>
```

#### Example Usage

```typescript
import { cancelAppointment } from '@/services/appointments';

await cancelAppointment('apt_345678', 'Patient requested cancellation');
```

## Medical Records

### createMedicalRecord

Creates a new medical record for a patient.

#### Signature

```typescript
function createMedicalRecord(
  recordData: CreateMedicalRecordRequest
): Promise<MedicalRecord>
```

#### CreateMedicalRecordRequest Interface

```typescript
interface CreateMedicalRecordRequest {
  patientId: string;
  doctorId: string;
  appointmentId?: string;
  recordType: 'consultation' | 'diagnosis' | 'treatment' | 'lab_result' | 'imaging';
  diagnosis?: {
    primary: string;
    secondary?: string[];
    icdCodes?: string[];
  };
  treatment?: {
    medications?: Medication[];
    procedures?: Procedure[];
    recommendations?: string[];
  };
  vitals?: {
    bloodPressure?: string;
    heartRate?: number;
    temperature?: number;
    weight?: number;
    height?: number;
  };
  notes: string;
  attachments?: string[];    // File IDs
}
```

#### Example Usage

```typescript
import { createMedicalRecord } from '@/services/medical-records';

const recordData = {
  patientId: 'pat_123456',
  doctorId: 'doc_789012',
  appointmentId: 'apt_345678',
  recordType: 'consultation',
  diagnosis: {
    primary: 'Hypertension',
    icdCodes: ['I10']
  },
  treatment: {
    medications: [{
      name: 'Lisinopril',
      dosage: '10mg',
      frequency: 'once daily',
      duration: '30 days'
    }],
    recommendations: [
      'Low sodium diet',
      'Regular exercise',
      'Monitor blood pressure'
    ]
  },
  vitals: {
    bloodPressure: '140/90',
    heartRate: 78,
    weight: 180
  },
  notes: 'Patient presents with elevated blood pressure. Started on ACE inhibitor.'
};

const record = await createMedicalRecord(recordData);
```

### searchMedicalRecords

Searches medical records with various filters.

#### Signature

```typescript
function searchMedicalRecords(
  criteria: MedicalRecordSearchCriteria,
  options?: SearchOptions
): Promise<SearchResult<MedicalRecord>>
```

#### MedicalRecordSearchCriteria Interface

```typescript
interface MedicalRecordSearchCriteria {
  patientId?: string;
  doctorId?: string;
  recordType?: string;
  diagnosisKeywords?: string[];
  dateRange?: {
    from: string;
    to: string;
  };
  icdCodes?: string[];
}
```

#### Example Usage

```typescript
import { searchMedicalRecords } from '@/services/medical-records';

const criteria = {
  patientId: 'pat_123456',
  recordType: 'consultation',
  dateRange: {
    from: '2024-01-01',
    to: '2024-01-31'
  }
};

const records = await searchMedicalRecords(criteria, { limit: 50 });
```

## Staff Management

### createStaff

Creates a new staff member record.

#### Signature

```typescript
function createStaff(
  staffData: CreateStaffRequest
): Promise<Staff>
```

#### CreateStaffRequest Interface

```typescript
interface CreateStaffRequest {
  personalInfo: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    dateOfBirth: string;
  };
  employmentInfo: {
    employeeId: string;
    role: 'doctor' | 'nurse' | 'admin' | 'technician' | 'receptionist';
    department: string;
    startDate: string;
    licenseNumber?: string;
    specializations?: string[];
  };
  credentials: {
    email: string;
    temporaryPassword: string;
  };
}
```

#### Example Usage

```typescript
import { createStaff } from '@/services/staff';

const staffData = {
  personalInfo: {
    firstName: 'Dr. Sarah',
    lastName: 'Johnson',
    email: 'sarah.johnson@hospital.com',
    phone: '+1-555-0150',
    dateOfBirth: '1980-05-15'
  },
  employmentInfo: {
    employeeId: 'EMP001',
    role: 'doctor',
    department: 'cardiology',
    startDate: '2024-01-01',
    licenseNumber: 'MD123456',
    specializations: ['Interventional Cardiology', 'Echocardiography']
  },
  credentials: {
    email: 'sarah.johnson@hospital.com',
    temporaryPassword: 'TempPass123!'
  }
};

const staff = await createStaff(staffData);
```

### updateStaffSchedule

Updates a staff member's schedule.

#### Signature

```typescript
function updateStaffSchedule(
  staffId: string,
  schedule: StaffSchedule
): Promise<void>
```

#### StaffSchedule Interface

```typescript
interface StaffSchedule {
  weeklyHours: {
    [day: string]: {
      startTime: string;
      endTime: string;
      breaks?: {
        startTime: string;
        endTime: string;
      }[];
    };
  };
  exceptions?: {
    date: string;
    type: 'off' | 'modified';
    startTime?: string;
    endTime?: string;
  }[];
}
```

#### Example Usage

```typescript
import { updateStaffSchedule } from '@/services/staff';

const schedule = {
  weeklyHours: {
    monday: { startTime: '08:00', endTime: '17:00' },
    tuesday: { startTime: '08:00', endTime: '17:00' },
    wednesday: { startTime: '08:00', endTime: '17:00' },
    thursday: { startTime: '08:00', endTime: '17:00' },
    friday: { startTime: '08:00', endTime: '17:00' }
  },
  exceptions: [
    {
      date: '2024-01-25',
      type: 'off'
    }
  ]
};

await updateStaffSchedule('staff_123', schedule);
```

## Utility Functions

### validateEmail

Validates an email address format.

#### Signature

```typescript
function validateEmail(email: string): boolean
```

#### Example Usage

```typescript
import { validateEmail } from '@/utils/validation';

if (!validateEmail(userEmail)) {
  throw new ValidationError('Invalid email format');
}
```

### formatPhoneNumber

Formats a phone number to a standard format.

#### Signature

```typescript
function formatPhoneNumber(
  phone: string,
  countryCode?: string
): string
```

#### Example Usage

```typescript
import { formatPhoneNumber } from '@/utils/formatting';

const formatted = formatPhoneNumber('5551234567', 'US');
// Returns: '+1-555-123-4567'
```

### generateMRN

Generates a unique Medical Record Number.

#### Signature

```typescript
function generateMRN(): string
```

#### Example Usage

```typescript
import { generateMRN } from '@/utils/generators';

const mrn = generateMRN();
// Returns: 'MRN-2024-001234'
```

### calculateAge

Calculates age from date of birth.

#### Signature

```typescript
function calculateAge(dateOfBirth: string): number
```

#### Example Usage

```typescript
import { calculateAge } from '@/utils/date';

const age = calculateAge('1985-03-15');
// Returns: 38 (as of 2024)
```

### hashPassword

Securely hashes a password using bcrypt.

#### Signature

```typescript
function hashPassword(
  password: string,
  saltRounds?: number
): Promise<string>
```

#### Example Usage

```typescript
import { hashPassword } from '@/utils/security';

const hashedPassword = await hashPassword('userPassword123');
```

### comparePassword

Compares a plain text password with a hashed password.

#### Signature

```typescript
function comparePassword(
  password: string,
  hashedPassword: string
): Promise<boolean>
```

#### Example Usage

```typescript
import { comparePassword } from '@/utils/security';

const isValid = await comparePassword(inputPassword, storedHash);
```

## Database Functions

### executeQuery

Executes a SQL query with parameterized inputs.

#### Signature

```typescript
function executeQuery<T>(
  query: string,
  params?: any[]
): Promise<T[]>
```

#### Example Usage

```typescript
import { executeQuery } from '@/utils/database';

const patients = await executeQuery<Patient>(
  'SELECT * FROM patients WHERE department = ? AND status = ?',
  ['cardiology', 'active']
);
```

### beginTransaction

Starts a database transaction.

#### Signature

```typescript
function beginTransaction(): Promise<Transaction>
```

#### Example Usage

```typescript
import { beginTransaction } from '@/utils/database';

const transaction = await beginTransaction();

try {
  await transaction.query('INSERT INTO patients ...');
  await transaction.query('INSERT INTO medical_records ...');
  await transaction.commit();
} catch (error) {
  await transaction.rollback();
  throw error;
}
```

## File Management

### uploadFile

Uploads a file to the storage system.

#### Signature

```typescript
function uploadFile(
  file: Buffer | ReadableStream,
  metadata: FileMetadata
): Promise<FileUploadResult>
```

#### FileMetadata Interface

```typescript
interface FileMetadata {
  filename: string;
  contentType: string;
  size: number;
  category: 'medical_record' | 'profile_image' | 'document';
  associatedRecordId?: string;
}
```

#### Example Usage

```typescript
import { uploadFile } from '@/services/files';

const fileMetadata = {
  filename: 'lab_results.pdf',
  contentType: 'application/pdf',
  size: fileBuffer.length,
  category: 'medical_record',
  associatedRecordId: 'rec_123456'
};

const result = await uploadFile(fileBuffer, fileMetadata);
console.log('File uploaded:', result.fileId);
```

### downloadFile

Downloads a file from the storage system.

#### Signature

```typescript
function downloadFile(fileId: string): Promise<FileDownloadResult>
```

#### Example Usage

```typescript
import { downloadFile } from '@/services/files';

const file = await downloadFile('file_123456');
// file.stream contains the file data
// file.metadata contains file information
```

## Notification Functions

### sendNotification

Sends a notification to users via various channels.

#### Signature

```typescript
function sendNotification(
  notification: NotificationRequest
): Promise<void>
```

#### NotificationRequest Interface

```typescript
interface NotificationRequest {
  recipients: string[];       // User IDs
  type: 'email' | 'sms' | 'push' | 'in_app';
  subject?: string;
  message: string;
  priority: 'low' | 'normal' | 'high';
  data?: Record<string, any>; // Additional data for templates
}
```

#### Example Usage

```typescript
import { sendNotification } from '@/services/notifications';

await sendNotification({
  recipients: ['user_123', 'user_456'],
  type: 'email',
  subject: 'Appointment Reminder',
  message: 'You have an appointment tomorrow at 10:00 AM',
  priority: 'normal',
  data: {
    appointmentId: 'apt_789',
    patientName: 'John Doe'
  }
});
```

### scheduleNotification

Schedules a notification to be sent at a future time.

#### Signature

```typescript
function scheduleNotification(
  notification: NotificationRequest,
  scheduledTime: string
): Promise<string>
```

#### Example Usage

```typescript
import { scheduleNotification } from '@/services/notifications';

const jobId = await scheduleNotification(
  {
    recipients: ['user_123'],
    type: 'sms',
    message: 'Appointment reminder: Tomorrow at 2:00 PM'
  },
  '2024-01-24T18:00:00Z'
);
```

## Error Handling

### Custom Error Classes

#### ValidationError

```typescript
class ValidationError extends Error {
  public details: ValidationDetail[];
  
  constructor(message: string, details: ValidationDetail[]) {
    super(message);
    this.name = 'ValidationError';
    this.details = details;
  }
}
```

#### DatabaseError

```typescript
class DatabaseError extends Error {
  public code: string;
  public query?: string;
  
  constructor(message: string, code: string, query?: string) {
    super(message);
    this.name = 'DatabaseError';
    this.code = code;
    this.query = query;
  }
}
```

#### AuthenticationError

```typescript
class AuthenticationError extends Error {
  constructor(message: string = 'Authentication failed') {
    super(message);
    this.name = 'AuthenticationError';
  }
}
```

## Testing Utilities

### createTestData

Creates test data for development and testing.

#### Signature

```typescript
function createTestData(
  type: 'patient' | 'appointment' | 'staff',
  count?: number
): Promise<void>
```

#### Example Usage

```typescript
import { createTestData } from '@/utils/testing';

// Create 100 test patients
await createTestData('patient', 100);
```

### mockApiCall

Mocks API calls for testing purposes.

#### Signature

```typescript
function mockApiCall<T>(
  endpoint: string,
  response: T,
  delay?: number
): void
```

#### Example Usage

```typescript
import { mockApiCall } from '@/utils/testing';

// Mock patient API call
mockApiCall('/api/patients/123', {
  id: 'pat_123',
  firstName: 'John',
  lastName: 'Doe'
}, 500); // 500ms delay
```

## Performance & Monitoring

### logPerformance

Logs performance metrics for functions.

#### Signature

```typescript
function logPerformance(
  functionName: string,
  executionTime: number,
  metadata?: Record<string, any>
): void
```

#### Example Usage

```typescript
import { logPerformance } from '@/utils/monitoring';

async function searchPatients(criteria: SearchCriteria) {
  const startTime = Date.now();
  
  try {
    const results = await findPatients(criteria);
    
    logPerformance('searchPatients', Date.now() - startTime, {
      resultCount: results.data.length,
      criteria: JSON.stringify(criteria)
    });
    
    return results;
  } catch (error) {
    logPerformance('searchPatients', Date.now() - startTime, {
      error: error.message
    });
    throw error;
  }
}
```

### cacheResult

Caches function results for improved performance.

#### Signature

```typescript
function cacheResult<T>(
  key: string,
  getValue: () => Promise<T>,
  ttl?: number
): Promise<T>
```

#### Example Usage

```typescript
import { cacheResult } from '@/utils/caching';

async function getDepartments() {
  return cacheResult(
    'departments',
    async () => {
      return await executeQuery('SELECT * FROM departments');
    },
    300 // 5 minutes TTL
  );
}
```

## Configuration

### getConfig

Retrieves configuration values.

#### Signature

```typescript
function getConfig(key: string, defaultValue?: any): any
```

#### Example Usage

```typescript
import { getConfig } from '@/utils/config';

const dbUrl = getConfig('DATABASE_URL');
const maxFileSize = getConfig('MAX_FILE_SIZE', 10485760); // 10MB default
```

### Environment Variables

Key configuration environment variables:

```bash
# Database
DATABASE_URL=postgresql://user:pass@localhost:5432/hospital
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=1h

# File Storage
STORAGE_PROVIDER=s3|local
S3_BUCKET=hospital-files
S3_REGION=us-east-1

# Email
SMTP_HOST=smtp.hospital.com
SMTP_PORT=587
SMTP_USER=notifications@hospital.com

# Monitoring
LOG_LEVEL=info
METRICS_ENABLED=true
```

## Best Practices

### Function Design

1. **Single Responsibility**: Each function should have one clear purpose
2. **Type Safety**: Use TypeScript interfaces and types
3. **Error Handling**: Always handle and propagate errors appropriately
4. **Documentation**: Include JSDoc comments for all public functions
5. **Testing**: Write unit tests for all functions

### Performance Optimization

1. **Caching**: Cache frequently accessed data
2. **Pagination**: Always paginate large result sets
3. **Indexing**: Ensure database queries use proper indexes
4. **Async/Await**: Use async operations for I/O intensive tasks
5. **Connection Pooling**: Use connection pools for database access

### Security Considerations

1. **Input Validation**: Validate all inputs
2. **SQL Injection**: Use parameterized queries
3. **Authentication**: Verify user permissions
4. **Encryption**: Encrypt sensitive data
5. **Audit Logging**: Log security-related events

## Contributing

When adding new functions:

1. Follow TypeScript best practices
2. Include comprehensive parameter validation
3. Write unit and integration tests
4. Document all parameters and return values
5. Add error handling and logging
6. Update this documentation

## Support

- API Documentation: https://docs.hospital.com/api
- Function Reference: https://docs.hospital.com/functions
- GitHub Issues: https://github.com/hospital/backend/issues