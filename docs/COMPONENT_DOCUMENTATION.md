# Component Documentation

## Overview

This document provides comprehensive documentation for all reusable UI components in the Hospital Enterprise Application. Components are built using modern design principles with accessibility, responsiveness, and usability in mind.

## Design System

### Color Palette

```css
:root {
  /* Primary Colors */
  --primary-50: #f0f9ff;
  --primary-500: #3b82f6;
  --primary-600: #2563eb;
  --primary-700: #1d4ed8;

  /* Secondary Colors */
  --secondary-50: #f8fafc;
  --secondary-500: #64748b;
  --secondary-600: #475569;
  --secondary-700: #334155;

  /* Status Colors */
  --success-500: #10b981;
  --warning-500: #f59e0b;
  --error-500: #ef4444;
  --info-500: #06b6d4;

  /* Neutral Colors */
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-500: #6b7280;
  --gray-900: #111827;
}
```

### Typography

```css
/* Font Families */
--font-sans: 'Inter', system-ui, sans-serif;
--font-mono: 'JetBrains Mono', monospace;

/* Font Sizes */
--text-xs: 0.75rem;
--text-sm: 0.875rem;
--text-base: 1rem;
--text-lg: 1.125rem;
--text-xl: 1.25rem;
--text-2xl: 1.5rem;
--text-3xl: 1.875rem;
```

### Spacing

```css
/* Spacing Scale */
--space-1: 0.25rem;
--space-2: 0.5rem;
--space-3: 0.75rem;
--space-4: 1rem;
--space-6: 1.5rem;
--space-8: 2rem;
--space-12: 3rem;
--space-16: 4rem;
```

## Core Components

### Button

A versatile button component with multiple variants and states.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `variant` | `'primary' \| 'secondary' \| 'outline' \| 'ghost' \| 'destructive'` | `'primary'` | Visual style variant |
| `size` | `'sm' \| 'md' \| 'lg' \| 'xl'` | `'md'` | Size of the button |
| `disabled` | `boolean` | `false` | Whether the button is disabled |
| `loading` | `boolean` | `false` | Whether to show loading state |
| `icon` | `ReactNode` | `undefined` | Icon to display in the button |
| `iconPosition` | `'left' \| 'right'` | `'left'` | Position of the icon |
| `fullWidth` | `boolean` | `false` | Whether button should take full width |
| `onClick` | `(event: MouseEvent) => void` | `undefined` | Click handler |
| `children` | `ReactNode` | `undefined` | Button content |

#### Usage Examples

```tsx
import { Button } from '@/components/ui/Button';
import { PlusIcon, LoaderIcon } from '@/components/icons';

// Basic button
<Button onClick={() => console.log('clicked')}>
  Save Patient
</Button>

// Primary button with icon
<Button variant="primary" icon={<PlusIcon />}>
  Add New Patient
</Button>

// Loading state
<Button loading disabled>
  Saving...
</Button>

// Destructive action
<Button variant="destructive" onClick={handleDelete}>
  Delete Record
</Button>

// Full width button
<Button fullWidth>
  Schedule Appointment
</Button>

// Different sizes
<Button size="sm">Small</Button>
<Button size="md">Medium</Button>
<Button size="lg">Large</Button>
<Button size="xl">Extra Large</Button>
```

#### Accessibility

- Uses semantic `<button>` element
- Supports keyboard navigation (Enter, Space)
- Includes ARIA attributes for screen readers
- Loading state announces to screen readers
- Focus management with visible focus indicators

### Input

A flexible input component supporting various types and validation states.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `type` | `'text' \| 'email' \| 'password' \| 'number' \| 'tel' \| 'url'` | `'text'` | Input type |
| `label` | `string` | `undefined` | Label text |
| `placeholder` | `string` | `undefined` | Placeholder text |
| `value` | `string` | `undefined` | Controlled value |
| `defaultValue` | `string` | `undefined` | Default value |
| `error` | `string` | `undefined` | Error message |
| `helperText` | `string` | `undefined` | Helper text |
| `required` | `boolean` | `false` | Whether field is required |
| `disabled` | `boolean` | `false` | Whether input is disabled |
| `icon` | `ReactNode` | `undefined` | Icon to display |
| `iconPosition` | `'left' \| 'right'` | `'left'` | Icon position |
| `onChange` | `(value: string) => void` | `undefined` | Change handler |

#### Usage Examples

```tsx
import { Input } from '@/components/ui/Input';
import { UserIcon, EmailIcon } from '@/components/icons';

// Basic input
<Input
  label="Patient Name"
  placeholder="Enter patient name"
  value={patientName}
  onChange={setPatientName}
/>

// Input with validation
<Input
  type="email"
  label="Email Address"
  value={email}
  error={emailError}
  required
  onChange={setEmail}
/>

// Input with icon
<Input
  label="Phone Number"
  type="tel"
  icon={<PhoneIcon />}
  value={phone}
  onChange={setPhone}
/>

// Disabled input
<Input
  label="Medical Record Number"
  value={mrn}
  disabled
  helperText="Auto-generated upon patient creation"
/>
```

### PatientCard

A card component for displaying patient information in lists and grids.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `patient` | `Patient` | `required` | Patient data object |
| `variant` | `'compact' \| 'detailed'` | `'detailed'` | Display variant |
| `showActions` | `boolean` | `true` | Whether to show action buttons |
| `onEdit` | `(patient: Patient) => void` | `undefined` | Edit handler |
| `onView` | `(patient: Patient) => void` | `undefined` | View handler |
| `onDelete` | `(patient: Patient) => void` | `undefined` | Delete handler |

#### Usage Examples

```tsx
import { PatientCard } from '@/components/PatientCard';

const patient = {
  id: 'pat_123456',
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1985-03-15',
  gender: 'male',
  phone: '+1-555-0123',
  email: 'john.doe@email.com',
  bloodType: 'O+',
  lastVisit: '2024-01-15T10:30:00Z'
};

// Detailed card
<PatientCard
  patient={patient}
  variant="detailed"
  onEdit={handleEdit}
  onView={handleView}
  onDelete={handleDelete}
/>

// Compact card
<PatientCard
  patient={patient}
  variant="compact"
  showActions={false}
/>
```

### AppointmentCalendar

A calendar component for scheduling and viewing appointments.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `appointments` | `Appointment[]` | `[]` | Array of appointments |
| `selectedDate` | `Date` | `new Date()` | Currently selected date |
| `view` | `'month' \| 'week' \| 'day'` | `'month'` | Calendar view |
| `onDateSelect` | `(date: Date) => void` | `undefined` | Date selection handler |
| `onAppointmentClick` | `(appointment: Appointment) => void` | `undefined` | Appointment click handler |
| `onSlotClick` | `(date: Date, time: string) => void` | `undefined` | Time slot click handler |

#### Usage Examples

```tsx
import { AppointmentCalendar } from '@/components/AppointmentCalendar';

<AppointmentCalendar
  appointments={appointments}
  selectedDate={selectedDate}
  view="week"
  onDateSelect={setSelectedDate}
  onAppointmentClick={handleAppointmentClick}
  onSlotClick={handleSlotClick}
/>
```

### MedicalRecordForm

A comprehensive form for creating and editing medical records.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `patientId` | `string` | `required` | Patient identifier |
| `record` | `MedicalRecord` | `undefined` | Existing record for editing |
| `onSave` | `(record: MedicalRecord) => void` | `required` | Save handler |
| `onCancel` | `() => void` | `required` | Cancel handler |
| `readOnly` | `boolean` | `false` | Whether form is read-only |

#### Usage Examples

```tsx
import { MedicalRecordForm } from '@/components/MedicalRecordForm';

// Create new record
<MedicalRecordForm
  patientId="pat_123456"
  onSave={handleSave}
  onCancel={handleCancel}
/>

// Edit existing record
<MedicalRecordForm
  patientId="pat_123456"
  record={existingRecord}
  onSave={handleSave}
  onCancel={handleCancel}
/>

// Read-only view
<MedicalRecordForm
  patientId="pat_123456"
  record={record}
  readOnly
  onCancel={handleClose}
/>
```

### SearchFilter

A component for searching and filtering data with multiple criteria.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `searchValue` | `string` | `''` | Current search value |
| `filters` | `FilterOption[]` | `[]` | Available filter options |
| `selectedFilters` | `Record<string, any>` | `{}` | Currently selected filters |
| `onSearchChange` | `(value: string) => void` | `undefined` | Search change handler |
| `onFilterChange` | `(filters: Record<string, any>) => void` | `undefined` | Filter change handler |
| `onClear` | `() => void` | `undefined` | Clear all handler |

#### Usage Examples

```tsx
import { SearchFilter } from '@/components/SearchFilter';

const filterOptions = [
  {
    key: 'department',
    label: 'Department',
    type: 'select',
    options: [
      { value: 'cardiology', label: 'Cardiology' },
      { value: 'neurology', label: 'Neurology' }
    ]
  },
  {
    key: 'status',
    label: 'Status',
    type: 'multiselect',
    options: [
      { value: 'active', label: 'Active' },
      { value: 'inactive', label: 'Inactive' }
    ]
  }
];

<SearchFilter
  searchValue={searchQuery}
  filters={filterOptions}
  selectedFilters={selectedFilters}
  onSearchChange={setSearchQuery}
  onFilterChange={setSelectedFilters}
  onClear={clearFilters}
/>
```

## Layout Components

### Dashboard

The main dashboard layout component.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `user` | `User` | `required` | Current user data |
| `children` | `ReactNode` | `required` | Dashboard content |
| `sidebarCollapsed` | `boolean` | `false` | Whether sidebar is collapsed |

### Sidebar

Navigation sidebar component.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `collapsed` | `boolean` | `false` | Whether sidebar is collapsed |
| `activeItem` | `string` | `undefined` | Currently active menu item |
| `onItemClick` | `(item: string) => void` | `undefined` | Menu item click handler |

### Header

Application header with navigation and user menu.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `user` | `User` | `required` | Current user data |
| `onMenuToggle` | `() => void` | `undefined` | Mobile menu toggle handler |
| `onLogout` | `() => void` | `undefined` | Logout handler |

## Form Components

### FormField

A wrapper component for form fields with consistent styling.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `label` | `string` | `undefined` | Field label |
| `error` | `string` | `undefined` | Error message |
| `required` | `boolean` | `false` | Whether field is required |
| `children` | `ReactNode` | `required` | Form field content |

### DatePicker

A date picker component with calendar popup.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `value` | `Date` | `undefined` | Selected date |
| `onChange` | `(date: Date) => void` | `undefined` | Date change handler |
| `minDate` | `Date` | `undefined` | Minimum selectable date |
| `maxDate` | `Date` | `undefined` | Maximum selectable date |
| `placeholder` | `string` | `'Select date'` | Placeholder text |

### TimePicker

A time picker component for selecting time.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `value` | `string` | `undefined` | Selected time (HH:mm format) |
| `onChange` | `(time: string) => void` | `undefined` | Time change handler |
| `interval` | `number` | `15` | Time interval in minutes |
| `minTime` | `string` | `'00:00'` | Minimum selectable time |
| `maxTime` | `string` | `'23:59'` | Maximum selectable time |

## Data Display Components

### Table

A flexible table component with sorting, pagination, and selection.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `columns` | `Column[]` | `required` | Table column definitions |
| `data` | `any[]` | `required` | Table data |
| `loading` | `boolean` | `false` | Whether table is loading |
| `sortable` | `boolean` | `true` | Whether columns are sortable |
| `selectable` | `boolean` | `false` | Whether rows are selectable |
| `pagination` | `PaginationConfig` | `undefined` | Pagination configuration |

#### Usage Examples

```tsx
import { Table } from '@/components/ui/Table';

const columns = [
  {
    key: 'name',
    label: 'Patient Name',
    sortable: true,
    render: (value, row) => `${row.firstName} ${row.lastName}`
  },
  {
    key: 'dateOfBirth',
    label: 'Date of Birth',
    sortable: true,
    render: (value) => new Date(value).toLocaleDateString()
  },
  {
    key: 'actions',
    label: 'Actions',
    render: (value, row) => (
      <div>
        <Button size="sm" onClick={() => handleEdit(row)}>Edit</Button>
        <Button size="sm" variant="ghost" onClick={() => handleView(row)}>View</Button>
      </div>
    )
  }
];

<Table
  columns={columns}
  data={patients}
  loading={loading}
  sortable
  selectable
  pagination={{
    currentPage: 1,
    totalPages: 10,
    pageSize: 20,
    onPageChange: setCurrentPage
  }}
/>
```

### Card

A container component for grouping related content.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `title` | `string` | `undefined` | Card title |
| `subtitle` | `string` | `undefined` | Card subtitle |
| `actions` | `ReactNode` | `undefined` | Action buttons/elements |
| `children` | `ReactNode` | `required` | Card content |
| `padding` | `'none' \| 'sm' \| 'md' \| 'lg'` | `'md'` | Internal padding |

### Badge

A small component for displaying status or category information.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `variant` | `'default' \| 'success' \| 'warning' \| 'error' \| 'info'` | `'default'` | Visual variant |
| `size` | `'sm' \| 'md' \| 'lg'` | `'md'` | Badge size |
| `children` | `ReactNode` | `required` | Badge content |

## Feedback Components

### Alert

A component for displaying important messages to users.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `type` | `'info' \| 'success' \| 'warning' \| 'error'` | `'info'` | Alert type |
| `title` | `string` | `undefined` | Alert title |
| `children` | `ReactNode` | `required` | Alert content |
| `dismissible` | `boolean` | `false` | Whether alert can be dismissed |
| `onDismiss` | `() => void` | `undefined` | Dismiss handler |

### Toast

A notification component for temporary messages.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `type` | `'info' \| 'success' \| 'warning' \| 'error'` | `'info'` | Toast type |
| `title` | `string` | `undefined` | Toast title |
| `message` | `string` | `required` | Toast message |
| `duration` | `number` | `5000` | Auto-dismiss duration (ms) |
| `onDismiss` | `() => void` | `undefined` | Dismiss handler |

### Modal

A modal dialog component for overlaying content.

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `open` | `boolean` | `false` | Whether modal is open |
| `title` | `string` | `undefined` | Modal title |
| `size` | `'sm' \| 'md' \| 'lg' \| 'xl' \| 'full'` | `'md'` | Modal size |
| `children` | `ReactNode` | `required` | Modal content |
| `footer` | `ReactNode` | `undefined` | Modal footer content |
| `onClose` | `() => void` | `undefined` | Close handler |

## Usage Guidelines

### Accessibility

All components follow WCAG 2.1 AA guidelines:

- Proper semantic HTML elements
- ARIA attributes for screen readers
- Keyboard navigation support
- Color contrast compliance
- Focus management

### Responsive Design

Components are built mobile-first with breakpoints:

```css
/* Breakpoints */
--breakpoint-sm: 640px;
--breakpoint-md: 768px;
--breakpoint-lg: 1024px;
--breakpoint-xl: 1280px;
```

### Performance

- Components use React.memo for optimization
- Heavy computations are memoized with useMemo
- Event handlers are memoized with useCallback
- Large lists use virtualization

### Testing

Each component includes:

- Unit tests with Jest and React Testing Library
- Accessibility tests
- Visual regression tests with Storybook
- E2E tests for complex interactions

### Storybook Integration

All components are documented in Storybook with:

- Interactive controls for all props
- Multiple usage examples
- Accessibility addon integration
- Design token documentation

## Contributing

When adding new components:

1. Follow the existing design system
2. Include comprehensive TypeScript types
3. Write unit and accessibility tests
4. Document all props and usage examples
5. Add Storybook stories
6. Update this documentation

## Support

- Component Library: https://storybook.hospital.com
- Design System: https://design.hospital.com
- GitHub Issues: https://github.com/hospital/frontend/issues