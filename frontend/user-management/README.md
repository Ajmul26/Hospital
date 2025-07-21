# User Management Frontend

A modern React TypeScript application for managing users with full CRUD functionality. This frontend interfaces with the Spring Boot backend API to provide a complete user management system.

## 🚀 Features

- **Dashboard**: Overview of system statistics and quick actions
- **User List**: View all users with search, sort, and filter capabilities
- **User Details**: Comprehensive view of individual user information
- **Add/Edit Users**: Form-based user creation and editing with validation
- **Real-time Search**: Search users by name, email, or phone
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Toast Notifications**: User feedback for all operations
- **Error Handling**: Graceful error handling with user-friendly messages

## 🛠️ Technology Stack

- **React 18** - Modern React with hooks
- **TypeScript** - Type-safe development
- **Material-UI v5** - Modern UI components and theming
- **React Router v6** - Client-side routing
- **React Hook Form** - Form handling with validation
- **Yup** - Schema validation
- **Axios** - HTTP client for API calls
- **React Toastify** - Toast notifications

## 📁 Project Structure

```
src/
├── components/          # React components
│   ├── Dashboard.tsx    # Dashboard with statistics
│   ├── Navbar.tsx       # Navigation bar
│   ├── UserList.tsx     # User list with data grid
│   ├── UserForm.tsx     # User creation/editing form
│   └── UserDetail.tsx   # User detail view
├── services/            # API services
│   └── api.ts          # Axios API client and endpoints
├── App.tsx             # Main app component with routing
├── index.tsx           # App entry point
└── ...
```

## 🏃‍♂️ Getting Started

### Prerequisites

- Node.js 16+ and npm
- Spring Boot backend running on `http://localhost:8080`

### Installation

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend/user-management
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Configure environment** (optional):
   ```bash
   # Copy and modify the environment file
   cp .env.example .env.local
   
   # Set your API URL (default: http://localhost:8080/api)
   REACT_APP_API_URL=http://localhost:8080/api
   ```

4. **Start the development server**:
   ```bash
   npm start
   ```

5. **Open your browser**:
   Navigate to `http://localhost:3000`

### Production Build

```bash
# Build for production
npm run build

# Serve the build (optional)
npx serve -s build -l 3000
```

## 🔧 Available Scripts

| Command | Description |
|---------|-------------|
| `npm start` | Start development server |
| `npm run build` | Build for production |
| `npm test` | Run tests |
| `npm run start:dev` | Start with explicit dev API URL |
| `npm run build:prod` | Build with production API URL |

## 🎨 UI Components

### Dashboard
- **Statistics Cards**: Total users, recent additions, API status
- **Quick Actions**: Direct access to common operations
- **Recent Users**: Latest user additions
- **System Information**: Backend and database info

### User List
- **Data Grid**: Sortable, paginated user table
- **Search**: Real-time search across all user fields
- **Actions**: View, edit, delete operations
- **Bulk Operations**: Select multiple users (future feature)

### User Form
- **Validation**: Real-time form validation with error messages
- **Email Uniqueness**: Automatic duplicate email checking
- **Responsive Layout**: Adapts to different screen sizes
- **Auto-save**: Form state preservation

### User Detail
- **Profile View**: User avatar and basic information
- **Contact Actions**: Direct email and phone links
- **System Info**: Creation and modification timestamps
- **Quick Actions**: Edit, delete, and communication options

## 🔌 API Integration

The frontend communicates with the Spring Boot backend through a well-defined REST API:

### Base Configuration
```typescript
// API base URL configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';
```

### Available Endpoints
- `GET /users` - Fetch all users
- `GET /users/{id}` - Fetch user by ID
- `POST /users` - Create new user
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user
- `GET /users/search?term={term}` - Search users
- `GET /users/health` - API health check

### Error Handling
- **Network Errors**: Automatic retry and user feedback
- **Validation Errors**: Field-level error display
- **API Errors**: Toast notifications with detailed messages
- **Loading States**: Visual feedback during operations

## 🎯 Features in Detail

### Real-time Search
```typescript
// Search implementation with API fallback
const handleSearch = async (searchValue: string) => {
  try {
    const results = await userApi.searchUsers(searchValue);
    setFilteredUsers(results);
  } catch (err) {
    // Fallback to local filtering
    const filtered = users.filter(user =>
      user.name.toLowerCase().includes(searchValue.toLowerCase())
    );
    setFilteredUsers(filtered);
  }
};
```

### Form Validation
```typescript
// Yup validation schema
const schema = yup.object({
  name: yup.string()
    .required('Name is required')
    .min(2, 'Name must be at least 2 characters')
    .max(50, 'Name must not exceed 50 characters'),
  email: yup.string()
    .required('Email is required')
    .email('Please enter a valid email address'),
  phone: yup.string()
    .max(15, 'Phone number must not exceed 15 characters')
    .matches(/^[+]?[0-9\s-()]*$/, 'Please enter a valid phone number')
});
```

### Responsive Design
```typescript
// Material-UI responsive grid system
<Grid container spacing={3}>
  <Grid item xs={12} md={6}>
    {/* Content adapts to screen size */}
  </Grid>
</Grid>
```

## 🌐 Environment Configuration

### Development
```bash
REACT_APP_API_URL=http://localhost:8080/api
```

### Production
```bash
REACT_APP_API_URL=https://your-api-domain.com/api
```

### Docker (future)
```bash
REACT_APP_API_URL=http://backend:8080/api
```

## 🔍 Debugging

### Console Logging
The application includes comprehensive logging:
- API requests and responses
- Error details and stack traces
- User action tracking
- Performance metrics

### Development Tools
- React Developer Tools
- Material-UI theme inspector
- Network tab monitoring
- Redux DevTools (if state management added)

## 📱 Mobile Responsiveness

The application is fully responsive and works on:
- **Desktop**: Full feature set with multi-column layouts
- **Tablet**: Condensed layouts with touch-friendly controls
- **Mobile**: Single-column layouts with optimized navigation

### Breakpoints
- `xs`: 0px and up (mobile)
- `sm`: 600px and up (small tablet)
- `md`: 900px and up (tablet)
- `lg`: 1200px and up (desktop)
- `xl`: 1536px and up (large desktop)

## 🚀 Performance Optimizations

- **Code Splitting**: Automatic route-based code splitting
- **Lazy Loading**: Components loaded on demand
- **Memoization**: React.memo for expensive renders
- **API Caching**: Request caching for static data
- **Image Optimization**: Optimized avatar and icon loading

## 🔒 Security Features

- **Input Sanitization**: All user inputs are sanitized
- **XSS Prevention**: React's built-in XSS protection
- **CORS Handling**: Proper CORS configuration
- **Environment Variables**: Sensitive data in env vars
- **API Validation**: Client-side validation mirrors backend

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
npm test

# Run tests with coverage
npm test -- --coverage

# Run tests in watch mode
npm test -- --watch
```

### Integration Tests
- API integration testing
- Component interaction testing
- User workflow testing

### E2E Tests (future)
- Cypress or Playwright integration
- Full user journey testing
- Cross-browser compatibility

## 📈 Future Enhancements

### Short-term
- [ ] Bulk user operations
- [ ] Advanced filtering
- [ ] Export functionality
- [ ] User roles and permissions

### Medium-term
- [ ] Dark mode support
- [ ] Offline functionality
- [ ] Real-time updates (WebSocket)
- [ ] Advanced analytics

### Long-term
- [ ] Progressive Web App (PWA)
- [ ] Multi-language support
- [ ] Advanced user management
- [ ] Integration with external services

## 🐛 Troubleshooting

### Common Issues

1. **API Connection Failed**
   - Ensure Spring Boot backend is running
   - Check `REACT_APP_API_URL` configuration
   - Verify CORS settings on backend

2. **Build Errors**
   - Clear node_modules and reinstall: `rm -rf node_modules && npm install`
   - Check Node.js version compatibility
   - Update dependencies: `npm update`

3. **Performance Issues**
   - Enable React Developer Tools Profiler
   - Check for unnecessary re-renders
   - Optimize component memoization

### Debug Mode
```bash
# Start with debug logging
REACT_APP_DEBUG=true npm start
```

## 📞 Support

For issues and feature requests:
1. Check the existing GitHub issues
2. Create a new issue with detailed description
3. Include browser and environment information
4. Provide steps to reproduce the problem

## 📄 License

This project is part of the Spring Boot CRUD application suite and follows the same licensing terms.
