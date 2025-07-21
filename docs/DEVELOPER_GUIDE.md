# Developer Guide

## Overview

This guide provides comprehensive instructions for setting up, developing, and contributing to the Hospital Enterprise Application. It covers everything from initial setup to deployment and best practices.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Project Structure](#project-structure)
3. [Development Environment](#development-environment)
4. [Coding Standards](#coding-standards)
5. [Testing Strategy](#testing-strategy)
6. [Database Management](#database-management)
7. [API Development](#api-development)
8. [Frontend Development](#frontend-development)
9. [Security Guidelines](#security-guidelines)
10. [Performance Optimization](#performance-optimization)
11. [Deployment](#deployment)
12. [Contributing](#contributing)

## Getting Started

### Prerequisites

Ensure you have the following installed:

- **Node.js** (v18+ recommended)
- **npm** or **yarn**
- **PostgreSQL** (v14+)
- **Redis** (v6+)
- **Docker** and **Docker Compose**
- **Git**

### Quick Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hospital/hospital-app.git
   cd hospital-app
   ```

2. **Install dependencies:**
   ```bash
   # Install backend dependencies
   cd backend
   npm install
   
   # Install frontend dependencies
   cd ../frontend
   npm install
   ```

3. **Set up environment variables:**
   ```bash
   # Copy example environment files
   cp backend/.env.example backend/.env
   cp frontend/.env.example frontend/.env
   
   # Edit the files with your configuration
   ```

4. **Start development services:**
   ```bash
   # Start with Docker Compose
   docker-compose up -d postgres redis
   
   # Or start manually
   # PostgreSQL: pg_ctl start
   # Redis: redis-server
   ```

5. **Initialize the database:**
   ```bash
   cd backend
   npm run db:migrate
   npm run db:seed
   ```

6. **Start the development servers:**
   ```bash
   # Terminal 1: Backend
   cd backend
   npm run dev
   
   # Terminal 2: Frontend
   cd frontend
   npm run dev
   ```

7. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/docs

## Project Structure

```
hospital-app/
├── backend/                 # Node.js/Express API server
│   ├── src/
│   │   ├── controllers/     # Route handlers
│   │   ├── services/        # Business logic
│   │   ├── models/          # Database models
│   │   ├── middleware/      # Express middleware
│   │   ├── utils/           # Utility functions
│   │   ├── routes/          # API routes
│   │   ├── config/          # Configuration files
│   │   └── types/           # TypeScript type definitions
│   ├── tests/               # Test files
│   ├── migrations/          # Database migrations
│   ├── seeds/               # Database seed files
│   └── docs/                # API documentation
├── frontend/                # React application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── pages/           # Page components
│   │   ├── hooks/           # Custom React hooks
│   │   ├── services/        # API client services
│   │   ├── utils/           # Utility functions
│   │   ├── styles/          # CSS/Styled components
│   │   ├── types/           # TypeScript types
│   │   └── contexts/        # React contexts
│   ├── public/              # Static assets
│   └── tests/               # Test files
├── shared/                  # Shared types and utilities
├── docs/                    # Project documentation
├── docker/                  # Docker configuration
├── scripts/                 # Build and deployment scripts
└── docker-compose.yml       # Development environment
```

## Development Environment

### Environment Variables

#### Backend (.env)

```bash
# Server Configuration
NODE_ENV=development
PORT=8080
API_BASE_URL=http://localhost:8080

# Database
DATABASE_URL=postgresql://hospital_user:password@localhost:5432/hospital_db
DATABASE_POOL_MIN=2
DATABASE_POOL_MAX=10

# Redis
REDIS_URL=redis://localhost:6379
REDIS_PASSWORD=

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRES_IN=24h
JWT_REFRESH_EXPIRES_IN=7d

# File Storage
STORAGE_PROVIDER=local
# For S3:
# STORAGE_PROVIDER=s3
# S3_BUCKET=hospital-files
# S3_REGION=us-east-1
# S3_ACCESS_KEY_ID=your-access-key
# S3_SECRET_ACCESS_KEY=your-secret-key

# Email Configuration
SMTP_HOST=localhost
SMTP_PORT=587
SMTP_USER=
SMTP_PASS=
SMTP_FROM=noreply@hospital.com

# Logging
LOG_LEVEL=debug
LOG_FILE=logs/application.log

# Security
BCRYPT_ROUNDS=12
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# External APIs
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_PHONE_NUMBER=

# Monitoring
SENTRY_DSN=
ENABLE_METRICS=true
```

#### Frontend (.env)

```bash
# API Configuration
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
REACT_APP_WS_URL=ws://localhost:8080

# Authentication
REACT_APP_JWT_STORAGE_KEY=hospital_token

# Feature Flags
REACT_APP_ENABLE_ANALYTICS=false
REACT_APP_ENABLE_CHAT=true
REACT_APP_ENABLE_NOTIFICATIONS=true

# External Services
REACT_APP_GOOGLE_MAPS_API_KEY=
REACT_APP_STRIPE_PUBLISHABLE_KEY=

# Development
GENERATE_SOURCEMAP=true
REACT_APP_LOG_LEVEL=debug
```

### IDE Configuration

#### VS Code Settings (.vscode/settings.json)

```json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true,
    "source.organizeImports": true
  },
  "typescript.preferences.importModuleSpecifier": "relative",
  "emmet.includeLanguages": {
    "javascript": "javascriptreact",
    "typescript": "typescriptreact"
  },
  "files.associations": {
    "*.css": "tailwindcss"
  }
}
```

#### Recommended Extensions

```json
{
  "recommendations": [
    "esbenp.prettier-vscode",
    "ms-vscode.vscode-typescript-next",
    "bradlc.vscode-tailwindcss",
    "ms-vscode.vscode-json",
    "formulahendry.auto-rename-tag",
    "christian-kohler.path-intellisense",
    "ms-vscode.vscode-eslint"
  ]
}
```

## Coding Standards

### TypeScript Configuration

#### Backend (tsconfig.json)

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "commonjs",
    "lib": ["ES2020"],
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true,
    "declaration": true,
    "declarationMap": true,
    "sourceMap": true,
    "baseUrl": "./src",
    "paths": {
      "@/*": ["*"],
      "@/controllers/*": ["controllers/*"],
      "@/services/*": ["services/*"],
      "@/models/*": ["models/*"],
      "@/utils/*": ["utils/*"]
    }
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist", "tests"]
}
```

#### Frontend (tsconfig.json)

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": [
      "dom",
      "dom.iterable",
      "es6"
    ],
    "allowJs": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "forceConsistentCasingInFileNames": true,
    "noFallthroughCasesInSwitch": true,
    "module": "esnext",
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "baseUrl": "./src",
    "paths": {
      "@/*": ["*"],
      "@/components/*": ["components/*"],
      "@/pages/*": ["pages/*"],
      "@/services/*": ["services/*"],
      "@/utils/*": ["utils/*"]
    }
  },
  "include": [
    "src"
  ]
}
```

### ESLint Configuration

#### Backend (.eslintrc.js)

```javascript
module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
  },
  extends: [
    '@typescript-eslint/recommended',
    'prettier',
  ],
  plugins: ['@typescript-eslint'],
  rules: {
    '@typescript-eslint/no-unused-vars': 'error',
    '@typescript-eslint/explicit-function-return-type': 'warn',
    '@typescript-eslint/no-explicit-any': 'warn',
    'prefer-const': 'error',
    'no-var': 'error',
  },
  env: {
    node: true,
    es2020: true,
  },
};
```

#### Frontend (.eslintrc.js)

```javascript
module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  extends: [
    'react-app',
    'react-app/jest',
    '@typescript-eslint/recommended',
    'prettier',
  ],
  plugins: ['@typescript-eslint', 'react-hooks'],
  rules: {
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
    '@typescript-eslint/no-unused-vars': 'error',
    'prefer-const': 'error',
    'no-var': 'error',
  },
  env: {
    browser: true,
    es2020: true,
  },
};
```

### Prettier Configuration (.prettierrc)

```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false
}
```

### Naming Conventions

#### Files and Directories

```
PascalCase:     Components, Classes, Types
camelCase:      Functions, variables, methods
kebab-case:     File names, URLs, CSS classes
UPPER_CASE:     Constants, environment variables
```

#### Examples

```typescript
// Files
PatientCard.tsx
userService.ts
api-client.ts

// Components
function PatientCard() { }
class UserService { }

// Functions and variables
const getUserData = () => { };
const patientCount = 42;

// Constants
const MAX_FILE_SIZE = 10485760;
const API_ENDPOINTS = {
  PATIENTS: '/patients',
  APPOINTMENTS: '/appointments'
};

// Types and Interfaces
interface User {
  id: string;
  email: string;
}

type UserRole = 'doctor' | 'nurse' | 'admin';
```

## Testing Strategy

### Backend Testing

#### Unit Tests (Jest + Supertest)

```typescript
// tests/services/patientService.test.ts
import { createPatient, findPatients } from '@/services/patientService';
import { PatientRepository } from '@/repositories/patientRepository';

jest.mock('@/repositories/patientRepository');

describe('PatientService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('createPatient', () => {
    it('should create a patient successfully', async () => {
      const mockPatient = {
        id: 'pat_123',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1985-03-15',
      };

      (PatientRepository.create as jest.Mock).mockResolvedValue(mockPatient);

      const result = await createPatient({
        personalInfo: {
          firstName: 'John',
          lastName: 'Doe',
          dateOfBirth: '1985-03-15',
          gender: 'male',
          contact: {
            phone: '+1-555-0123',
            address: {
              street: '123 Main St',
              city: 'City',
              state: 'ST',
              zipCode: '12345'
            }
          }
        },
        medicalInfo: {
          emergencyContact: {
            name: 'Jane Doe',
            relationship: 'spouse',
            phone: '+1-555-0124'
          }
        }
      });

      expect(result).toEqual(mockPatient);
      expect(PatientRepository.create).toHaveBeenCalledTimes(1);
    });

    it('should throw validation error for invalid data', async () => {
      await expect(createPatient({} as any)).rejects.toThrow('ValidationError');
    });
  });
});
```

#### Integration Tests

```typescript
// tests/integration/patients.test.ts
import request from 'supertest';
import { app } from '@/app';
import { getTestDatabase } from '@/utils/testDatabase';

describe('Patients API', () => {
  let database: any;

  beforeAll(async () => {
    database = await getTestDatabase();
  });

  afterAll(async () => {
    await database.cleanup();
  });

  beforeEach(async () => {
    await database.reset();
  });

  it('GET /api/v1/patients should return paginated patients', async () => {
    // Create test data
    await database.seed('patients', 5);

    const response = await request(app)
      .get('/api/v1/patients')
      .set('Authorization', 'Bearer ' + testToken)
      .expect(200);

    expect(response.body).toHaveProperty('patients');
    expect(response.body).toHaveProperty('pagination');
    expect(response.body.patients).toHaveLength(5);
  });

  it('POST /api/v1/patients should create a new patient', async () => {
    const patientData = {
      personalInfo: {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1985-03-15',
        gender: 'male',
        contact: {
          phone: '+1-555-0123',
          address: {
            street: '123 Main St',
            city: 'City',
            state: 'ST',
            zipCode: '12345'
          }
        }
      },
      medicalInfo: {
        emergencyContact: {
          name: 'Jane Doe',
          relationship: 'spouse',
          phone: '+1-555-0124'
        }
      }
    };

    const response = await request(app)
      .post('/api/v1/patients')
      .set('Authorization', 'Bearer ' + testToken)
      .send(patientData)
      .expect(201);

    expect(response.body).toHaveProperty('id');
    expect(response.body.personalInfo.firstName).toBe('John');
  });
});
```

### Frontend Testing

#### Component Tests (React Testing Library)

```tsx
// src/components/__tests__/PatientCard.test.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { PatientCard } from '@/components/PatientCard';

const mockPatient = {
  id: 'pat_123',
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1985-03-15',
  gender: 'male',
  phone: '+1-555-0123',
  email: 'john.doe@email.com',
  bloodType: 'O+',
  lastVisit: '2024-01-15T10:30:00Z'
};

describe('PatientCard', () => {
  it('renders patient information correctly', () => {
    render(<PatientCard patient={mockPatient} />);
    
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('john.doe@email.com')).toBeInTheDocument();
    expect(screen.getByText('+1-555-0123')).toBeInTheDocument();
  });

  it('calls onEdit when edit button is clicked', () => {
    const onEdit = jest.fn();
    render(<PatientCard patient={mockPatient} onEdit={onEdit} />);
    
    fireEvent.click(screen.getByText('Edit'));
    expect(onEdit).toHaveBeenCalledWith(mockPatient);
  });

  it('renders compact variant correctly', () => {
    render(<PatientCard patient={mockPatient} variant="compact" />);
    
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.queryByText('john.doe@email.com')).not.toBeInTheDocument();
  });
});
```

#### Hook Tests

```tsx
// src/hooks/__tests__/usePatients.test.tsx
import { renderHook, waitFor } from '@testing-library/react';
import { usePatients } from '@/hooks/usePatients';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import * as patientService from '@/services/patientService';

jest.mock('@/services/patientService');

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('usePatients', () => {
  it('fetches patients successfully', async () => {
    const mockPatients = [
      { id: 'pat_1', firstName: 'John', lastName: 'Doe' },
      { id: 'pat_2', firstName: 'Jane', lastName: 'Smith' },
    ];

    (patientService.getPatients as jest.Mock).mockResolvedValue({
      data: mockPatients,
      pagination: { total: 2, page: 1, totalPages: 1 }
    });

    const { result } = renderHook(() => usePatients(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.data).toEqual(mockPatients);
  });
});
```

### Test Commands

```bash
# Backend tests
npm run test                 # Run all tests
npm run test:watch          # Watch mode
npm run test:coverage       # With coverage report
npm run test:integration    # Integration tests only

# Frontend tests
npm run test                # Run all tests
npm run test:watch          # Watch mode
npm run test:coverage       # With coverage report
npm run test:e2e           # End-to-end tests
```

## Database Management

### Migrations

#### Creating Migrations

```bash
# Create a new migration
npm run db:migration:create add_patients_table

# Run migrations
npm run db:migrate

# Rollback last migration
npm run db:rollback

# Reset database
npm run db:reset
```

#### Migration Example

```typescript
// migrations/001_create_patients_table.ts
import { Knex } from 'knex';

export async function up(knex: Knex): Promise<void> {
  return knex.schema.createTable('patients', (table) => {
    table.uuid('id').primary().defaultTo(knex.raw('gen_random_uuid()'));
    table.string('medical_record_number').unique().notNullable();
    table.string('first_name').notNullable();
    table.string('last_name').notNullable();
    table.date('date_of_birth').notNullable();
    table.enum('gender', ['male', 'female', 'other']).notNullable();
    table.string('phone').notNullable();
    table.string('email');
    table.string('blood_type');
    table.json('allergies');
    table.json('address').notNullable();
    table.json('emergency_contact').notNullable();
    table.enum('status', ['active', 'inactive']).defaultTo('active');
    table.timestamps(true, true);
    
    table.index(['last_name', 'first_name']);
    table.index(['phone']);
    table.index(['email']);
    table.index(['status']);
  });
}

export async function down(knex: Knex): Promise<void> {
  return knex.schema.dropTable('patients');
}
```

### Seeds

```typescript
// seeds/001_patients.ts
import { Knex } from 'knex';
import { faker } from '@faker-js/faker';

export async function seed(knex: Knex): Promise<void> {
  await knex('patients').del();

  const patients = Array.from({ length: 100 }, () => ({
    medical_record_number: `MRN-${faker.number.int({ min: 100000, max: 999999 })}`,
    first_name: faker.person.firstName(),
    last_name: faker.person.lastName(),
    date_of_birth: faker.date.birthdate({ min: 18, max: 80, mode: 'age' }),
    gender: faker.helpers.arrayElement(['male', 'female', 'other']),
    phone: faker.phone.number(),
    email: faker.internet.email(),
    blood_type: faker.helpers.arrayElement(['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']),
    allergies: JSON.stringify(faker.helpers.arrayElements(['penicillin', 'latex', 'shellfish', 'peanuts'], { min: 0, max: 2 })),
    address: JSON.stringify({
      street: faker.location.streetAddress(),
      city: faker.location.city(),
      state: faker.location.state(),
      zipCode: faker.location.zipCode(),
    }),
    emergency_contact: JSON.stringify({
      name: faker.person.fullName(),
      relationship: faker.helpers.arrayElement(['spouse', 'parent', 'child', 'sibling', 'friend']),
      phone: faker.phone.number(),
    }),
  }));

  await knex('patients').insert(patients);
}
```

## API Development

### Controller Pattern

```typescript
// src/controllers/patientController.ts
import { Request, Response, NextFunction } from 'express';
import { PatientService } from '@/services/patientService';
import { validateCreatePatient } from '@/validators/patientValidator';
import { ApiResponse } from '@/types/api';

export class PatientController {
  static async getPatients(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const { page = 1, limit = 20, search, department, status } = req.query;
      
      const criteria = {
        ...(search && { query: search as string }),
        ...(department && { department: department as string }),
        ...(status && { status: status as string }),
      };

      const options = {
        page: parseInt(page as string, 10),
        limit: Math.min(parseInt(limit as string, 10), 100),
      };

      const result = await PatientService.findPatients(criteria, options);
      
      const response: ApiResponse<typeof result> = {
        success: true,
        data: result,
        message: 'Patients retrieved successfully',
      };

      res.json(response);
    } catch (error) {
      next(error);
    }
  }

  static async createPatient(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const validatedData = await validateCreatePatient(req.body);
      const patient = await PatientService.createPatient(validatedData);
      
      const response: ApiResponse<typeof patient> = {
        success: true,
        data: patient,
        message: 'Patient created successfully',
      };

      res.status(201).json(response);
    } catch (error) {
      next(error);
    }
  }

  static async getPatient(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const { id } = req.params;
      const patient = await PatientService.getPatientById(id);
      
      if (!patient) {
        return res.status(404).json({
          success: false,
          error: 'Patient not found',
        });
      }

      const response: ApiResponse<typeof patient> = {
        success: true,
        data: patient,
        message: 'Patient retrieved successfully',
      };

      res.json(response);
    } catch (error) {
      next(error);
    }
  }
}
```

### Validation

```typescript
// src/validators/patientValidator.ts
import Joi from 'joi';
import { CreatePatientRequest } from '@/types/patient';

const addressSchema = Joi.object({
  street: Joi.string().required(),
  city: Joi.string().required(),
  state: Joi.string().length(2).required(),
  zipCode: Joi.string().pattern(/^\d{5}(-\d{4})?$/).required(),
});

const emergencyContactSchema = Joi.object({
  name: Joi.string().required(),
  relationship: Joi.string().valid('spouse', 'parent', 'child', 'sibling', 'friend', 'other').required(),
  phone: Joi.string().pattern(/^\+?[\d\s\-\(\)]+$/).required(),
});

const createPatientSchema = Joi.object({
  personalInfo: Joi.object({
    firstName: Joi.string().min(1).max(50).required(),
    lastName: Joi.string().min(1).max(50).required(),
    dateOfBirth: Joi.date().iso().max('now').required(),
    gender: Joi.string().valid('male', 'female', 'other').required(),
    contact: Joi.object({
      phone: Joi.string().pattern(/^\+?[\d\s\-\(\)]+$/).required(),
      email: Joi.string().email().optional(),
      address: addressSchema.required(),
    }).required(),
  }).required(),
  medicalInfo: Joi.object({
    bloodType: Joi.string().valid('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-').optional(),
    allergies: Joi.array().items(Joi.string()).optional(),
    emergencyContact: emergencyContactSchema.required(),
  }).required(),
});

export async function validateCreatePatient(data: unknown): Promise<CreatePatientRequest> {
  const { error, value } = createPatientSchema.validate(data, { abortEarly: false });
  
  if (error) {
    throw new ValidationError('Invalid patient data', error.details);
  }
  
  return value;
}
```

### Middleware

```typescript
// src/middleware/auth.ts
import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { User } from '@/types/user';

interface AuthenticatedRequest extends Request {
  user?: User;
}

export function authenticateToken(req: AuthenticatedRequest, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({ error: 'Access token required' });
  }

  jwt.verify(token, process.env.JWT_SECRET!, (err, decoded) => {
    if (err) {
      return res.status(403).json({ error: 'Invalid or expired token' });
    }

    req.user = decoded as User;
    next();
  });
}

export function requireRole(role: string) {
  return (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
    if (!req.user || req.user.role !== role) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }
    next();
  };
}
```

## Frontend Development

### Component Structure

```tsx
// src/components/PatientCard/PatientCard.tsx
import React from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Patient } from '@/types/patient';
import { formatDate, calculateAge } from '@/utils/date';
import styles from './PatientCard.module.css';

interface PatientCardProps {
  patient: Patient;
  variant?: 'compact' | 'detailed';
  showActions?: boolean;
  onEdit?: (patient: Patient) => void;
  onView?: (patient: Patient) => void;
  onDelete?: (patient: Patient) => void;
}

export function PatientCard({
  patient,
  variant = 'detailed',
  showActions = true,
  onEdit,
  onView,
  onDelete,
}: PatientCardProps): JSX.Element {
  const age = calculateAge(patient.dateOfBirth);
  
  return (
    <Card className={styles.patientCard}>
      <div className={styles.header}>
        <div className={styles.nameSection}>
          <h3 className={styles.name}>
            {patient.personalInfo.firstName} {patient.personalInfo.lastName}
          </h3>
          <Badge variant={patient.status === 'active' ? 'success' : 'default'}>
            {patient.status}
          </Badge>
        </div>
        <div className={styles.mrn}>
          MRN: {patient.medicalRecordNumber}
        </div>
      </div>

      {variant === 'detailed' && (
        <div className={styles.details}>
          <div className={styles.info}>
            <span className={styles.label}>Age:</span>
            <span>{age} years</span>
          </div>
          <div className={styles.info}>
            <span className={styles.label}>Gender:</span>
            <span>{patient.personalInfo.gender}</span>
          </div>
          <div className={styles.info}>
            <span className={styles.label}>Phone:</span>
            <span>{patient.personalInfo.contact.phone}</span>
          </div>
          {patient.personalInfo.contact.email && (
            <div className={styles.info}>
              <span className={styles.label}>Email:</span>
              <span>{patient.personalInfo.contact.email}</span>
            </div>
          )}
          {patient.medicalInfo.bloodType && (
            <div className={styles.info}>
              <span className={styles.label}>Blood Type:</span>
              <span>{patient.medicalInfo.bloodType}</span>
            </div>
          )}
          {patient.lastVisit && (
            <div className={styles.info}>
              <span className={styles.label}>Last Visit:</span>
              <span>{formatDate(patient.lastVisit)}</span>
            </div>
          )}
        </div>
      )}

      {showActions && (
        <div className={styles.actions}>
          {onView && (
            <Button variant="outline" size="sm" onClick={() => onView(patient)}>
              View
            </Button>
          )}
          {onEdit && (
            <Button variant="primary" size="sm" onClick={() => onEdit(patient)}>
              Edit
            </Button>
          )}
          {onDelete && (
            <Button variant="destructive" size="sm" onClick={() => onDelete(patient)}>
              Delete
            </Button>
          )}
        </div>
      )}
    </Card>
  );
}
```

### Custom Hooks

```tsx
// src/hooks/usePatients.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientService } from '@/services/patientService';
import { SearchCriteria, SearchOptions } from '@/types/search';
import { Patient, CreatePatientRequest } from '@/types/patient';

export function usePatients(criteria?: SearchCriteria, options?: SearchOptions) {
  return useQuery({
    queryKey: ['patients', criteria, options],
    queryFn: () => patientService.getPatients(criteria, options),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

export function usePatient(patientId: string) {
  return useQuery({
    queryKey: ['patient', patientId],
    queryFn: () => patientService.getPatient(patientId),
    enabled: !!patientId,
  });
}

export function useCreatePatient() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreatePatientRequest) => patientService.createPatient(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}

export function useUpdatePatient() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Patient> }) =>
      patientService.updatePatient(id, data),
    onSuccess: (updatedPatient) => {
      queryClient.setQueryData(['patient', updatedPatient.id], updatedPatient);
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}

export function useDeletePatient() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (patientId: string) => patientService.deletePatient(patientId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });
}
```

### Service Layer

```typescript
// src/services/patientService.ts
import { apiClient } from '@/utils/apiClient';
import { Patient, CreatePatientRequest } from '@/types/patient';
import { SearchCriteria, SearchOptions, SearchResult } from '@/types/search';

export const patientService = {
  async getPatients(
    criteria?: SearchCriteria,
    options?: SearchOptions
  ): Promise<SearchResult<Patient>> {
    const params = new URLSearchParams();
    
    if (criteria?.query) params.append('search', criteria.query);
    if (criteria?.department) params.append('department', criteria.department);
    if (criteria?.status) params.append('status', criteria.status);
    if (options?.page) params.append('page', options.page.toString());
    if (options?.limit) params.append('limit', options.limit.toString());

    const response = await apiClient.get(`/patients?${params.toString()}`);
    return response.data;
  },

  async getPatient(patientId: string): Promise<Patient> {
    const response = await apiClient.get(`/patients/${patientId}`);
    return response.data;
  },

  async createPatient(data: CreatePatientRequest): Promise<Patient> {
    const response = await apiClient.post('/patients', data);
    return response.data;
  },

  async updatePatient(patientId: string, data: Partial<Patient>): Promise<Patient> {
    const response = await apiClient.put(`/patients/${patientId}`, data);
    return response.data;
  },

  async deletePatient(patientId: string): Promise<void> {
    await apiClient.delete(`/patients/${patientId}`);
  },
};
```

## Security Guidelines

### Authentication & Authorization

1. **JWT Token Management**
   - Store tokens securely (httpOnly cookies recommended)
   - Implement token refresh mechanism
   - Validate tokens on every request

2. **Password Security**
   - Use bcrypt with minimum 12 salt rounds
   - Enforce strong password policies
   - Implement password reset functionality

3. **Role-Based Access Control**
   - Define clear role hierarchies
   - Implement middleware for route protection
   - Check permissions at both frontend and backend

### Data Protection

1. **Input Validation**
   - Validate all inputs on both client and server
   - Use parameterized queries to prevent SQL injection
   - Sanitize data before database operations

2. **Data Encryption**
   - Encrypt sensitive data at rest
   - Use HTTPS for all communications
   - Implement field-level encryption for PII

3. **Audit Logging**
   - Log all security-related events
   - Track data access and modifications
   - Implement log monitoring and alerting

### Example Security Implementation

```typescript
// src/middleware/security.ts
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import { Request, Response, NextFunction } from 'express';

export const securityMiddleware = [
  helmet({
    contentSecurityPolicy: {
      directives: {
        defaultSrc: ["'self'"],
        styleSrc: ["'self'", "'unsafe-inline'"],
        scriptSrc: ["'self'"],
        imgSrc: ["'self'", "data:", "https:"],
      },
    },
  }),
  
  rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100, // limit each IP to 100 requests per windowMs
    message: 'Too many requests from this IP',
  }),
];

export function auditLog(action: string) {
  return (req: Request, res: Response, next: NextFunction) => {
    const { user } = req as any;
    const logData = {
      timestamp: new Date().toISOString(),
      userId: user?.id,
      action,
      resource: req.path,
      method: req.method,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
    };
    
    // Log to audit system
    console.log('AUDIT:', JSON.stringify(logData));
    
    next();
  };
}
```

## Performance Optimization

### Backend Optimization

1. **Database Optimization**
   - Use proper indexes
   - Implement connection pooling
   - Cache frequently accessed data

2. **API Optimization**
   - Implement response caching
   - Use compression middleware
   - Optimize database queries

3. **Monitoring**
   - Use APM tools (New Relic, DataDog)
   - Monitor response times
   - Track error rates

### Frontend Optimization

1. **Code Splitting**
   - Implement route-based splitting
   - Use dynamic imports
   - Lazy load components

2. **Caching Strategy**
   - Use React Query for server state
   - Implement service worker caching
   - Cache static assets

3. **Performance Monitoring**
   - Use Core Web Vitals
   - Monitor bundle sizes
   - Track rendering performance

## Deployment

### Production Environment

#### Backend Deployment

```dockerfile
# Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM node:18-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY package*.json ./

EXPOSE 8080

USER node

CMD ["node", "dist/index.js"]
```

#### Frontend Deployment

```dockerfile
# Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine AS runtime

COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose (Production)

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: hospital_prod
      POSTGRES_USER: hospital_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

  redis:
    image: redis:6-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    restart: unless-stopped

  backend:
    build:
      context: ./backend
      target: runtime
    environment:
      NODE_ENV: production
      DATABASE_URL: postgres://hospital_user:${POSTGRES_PASSWORD}@postgres:5432/hospital_prod
      REDIS_URL: redis://:${REDIS_PASSWORD}@redis:6379
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres
      - redis
    restart: unless-stopped

  frontend:
    build:
      context: ./frontend
      target: runtime
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend
    restart: unless-stopped

volumes:
  postgres_data:
```

### CI/CD Pipeline

#### GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: |
          cd backend && npm ci
          cd ../frontend && npm ci
      
      - name: Run tests
        run: |
          cd backend && npm test
          cd ../frontend && npm test
      
      - name: Run linting
        run: |
          cd backend && npm run lint
          cd ../frontend && npm run lint

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to production
        run: |
          # Add your deployment commands here
          echo "Deploying to production..."
```

## Contributing

### Development Workflow

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/hospital-app.git
   cd hospital-app
   git remote add upstream https://github.com/hospital/hospital-app.git
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/patient-management
   ```

3. **Make Changes**
   - Follow coding standards
   - Write tests
   - Update documentation

4. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: add patient management functionality"
   ```

5. **Push and Create PR**
   ```bash
   git push origin feature/patient-management
   ```

### Commit Convention

Use conventional commits:

```
feat: add new feature
fix: bug fix
docs: documentation changes
style: formatting changes
refactor: code refactoring
test: add or modify tests
chore: maintenance tasks
```

### Code Review Process

1. All PRs require at least 2 reviews
2. Automated tests must pass
3. Code coverage must be maintained
4. Documentation must be updated
5. Security review for sensitive changes

## Support and Resources

### Documentation
- [API Documentation](https://docs.hospital.com/api)
- [Component Library](https://storybook.hospital.com)
- [Database Schema](https://docs.hospital.com/database)

### Tools and Services
- [GitHub Repository](https://github.com/hospital/hospital-app)
- [Issue Tracker](https://github.com/hospital/hospital-app/issues)
- [CI/CD Pipeline](https://github.com/hospital/hospital-app/actions)

### Getting Help
- Slack: #hospital-dev
- Email: dev-team@hospital.com
- Office Hours: Tuesdays 2-4 PM EST

---

*This guide is a living document and should be updated as the project evolves.*