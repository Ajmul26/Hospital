import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Grid,
  Divider,
} from '@mui/material';
import {
  Save as SaveIcon,
  Cancel as CancelIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useParams, useNavigate } from 'react-router-dom';
import { userApi, User, CreateUserRequest, UpdateUserRequest } from '../services/api';
import { toast } from 'react-toastify';

// Validation schema
const schema = yup.object({
  name: yup
    .string()
    .required('Name is required')
    .min(2, 'Name must be at least 2 characters')
    .max(50, 'Name must not exceed 50 characters'),
  email: yup
    .string()
    .required('Email is required')
    .email('Please enter a valid email address'),
  phone: yup
    .string()
    .max(15, 'Phone number must not exceed 15 characters')
    .matches(/^[+]?[0-9\s-()]*$/, 'Please enter a valid phone number')
    .nullable()
    .transform((value, originalValue) => originalValue === '' ? null : value),
});

interface FormData {
  name: string;
  email: string;
  phone: string;
}

const UserForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  
  const [loading, setLoading] = useState(false);
  const [loadingUser, setLoadingUser] = useState(isEdit);
  const [error, setError] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
    setValue,
    watch,
  } = useForm<FormData>({
    resolver: yupResolver(schema),
    defaultValues: {
      name: '',
      email: '',
      phone: '',
    },
  });

  // Watch email for duplicate checking
  const emailValue = watch('email');

  const loadUser = async () => {
    if (!id) return;
    
    setLoadingUser(true);
    setError(null);
    
    try {
      const userData = await userApi.getUserById(parseInt(id));
      setUser(userData);
      
      // Populate form with user data
      setValue('name', userData.name);
      setValue('email', userData.email);
      setValue('phone', userData.phone || '');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load user';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoadingUser(false);
    }
  };

  const checkEmailExists = async (email: string) => {
    if (!email || (isEdit && user?.email === email)) return false;
    
    try {
      return await userApi.checkEmailExists(email);
    } catch (err) {
      return false;
    }
  };

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    setError(null);
    
    try {
      // Check for duplicate email
      const emailExists = await checkEmailExists(data.email);
      if (emailExists) {
        setError('Email address already exists. Please use a different email.');
        setLoading(false);
        return;
      }

      let result: User;
      
      if (isEdit && id) {
        // Update existing user
        const updateData: UpdateUserRequest = {
          name: data.name,
          email: data.email,
          phone: data.phone || undefined,
        };
        result = await userApi.updateUser(parseInt(id), updateData);
        toast.success(`User "${result.name}" updated successfully`);
      } else {
        // Create new user
        const createData: CreateUserRequest = {
          name: data.name,
          email: data.email,
          phone: data.phone || undefined,
        };
        result = await userApi.createUser(createData);
        toast.success(`User "${result.name}" created successfully`);
      }
      
      // Navigate to user detail page
      navigate(`/users/${result.id}`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 
        `Failed to ${isEdit ? 'update' : 'create'} user`;
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    if (isEdit && id) {
      navigate(`/users/${id}`);
    } else {
      navigate('/users');
    }
  };

  const handleReset = () => {
    if (isEdit && user) {
      setValue('name', user.name);
      setValue('email', user.email);
      setValue('phone', user.phone || '');
    } else {
      reset();
    }
    setError(null);
  };

  useEffect(() => {
    if (isEdit) {
      loadUser();
    }
  }, [id, isEdit]);

  if (loadingUser) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} />
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box display="flex" alignItems="center" mb={3}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/users')}
          sx={{ mr: 2 }}
        >
          Back to Users
        </Button>
        <Typography variant="h4" component="h1">
          {isEdit ? 'Edit User' : 'Add New User'}
        </Typography>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Form */}
      <Card>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              {/* User Information */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  User Information
                </Typography>
                <Divider sx={{ mb: 3 }} />
              </Grid>

              {/* Name Field */}
              <Grid item xs={12} md={6}>
                <Controller
                  name="name"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Full Name"
                      placeholder="Enter user's full name"
                      error={!!errors.name}
                      helperText={errors.name?.message}
                      required
                    />
                  )}
                />
              </Grid>

              {/* Email Field */}
              <Grid item xs={12} md={6}>
                <Controller
                  name="email"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Email Address"
                      placeholder="Enter email address"
                      type="email"
                      error={!!errors.email}
                      helperText={errors.email?.message}
                      required
                    />
                  )}
                />
              </Grid>

              {/* Phone Field */}
              <Grid item xs={12} md={6}>
                <Controller
                  name="phone"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Phone Number"
                      placeholder="Enter phone number (optional)"
                      error={!!errors.phone}
                      helperText={errors.phone?.message || 'Optional field'}
                    />
                  )}
                />
              </Grid>

              {/* User ID (Display only for edit) */}
              {isEdit && user && (
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="User ID"
                    value={user.id}
                    disabled
                    helperText="System generated ID"
                  />
                </Grid>
              )}

              {/* Timestamps (Display only for edit) */}
              {isEdit && user && (
                <>
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label="Created At"
                      value={new Date(user.createdAt).toLocaleString()}
                      disabled
                      helperText="Account creation date"
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label="Last Updated"
                      value={new Date(user.updatedAt).toLocaleString()}
                      disabled
                      helperText="Last modification date"
                    />
                  </Grid>
                </>
              )}

              {/* Form Actions */}
              <Grid item xs={12}>
                <Divider sx={{ my: 3 }} />
                <Box display="flex" gap={2} justifyContent="flex-end">
                  <Button
                    variant="outlined"
                    onClick={handleReset}
                    disabled={loading || isSubmitting}
                  >
                    Reset
                  </Button>
                  <Button
                    variant="outlined"
                    startIcon={<CancelIcon />}
                    onClick={handleCancel}
                    disabled={loading || isSubmitting}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    startIcon={loading || isSubmitting ? <CircularProgress size={20} /> : <SaveIcon />}
                    disabled={loading || isSubmitting}
                  >
                    {loading || isSubmitting ? 'Saving...' : isEdit ? 'Update User' : 'Create User'}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>

      {/* Form Tips */}
      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Form Guidelines
          </Typography>
          <Typography variant="body2" color="textSecondary" paragraph>
            • Name must be between 2-50 characters
          </Typography>
          <Typography variant="body2" color="textSecondary" paragraph>
            • Email address must be unique and valid
          </Typography>
          <Typography variant="body2" color="textSecondary" paragraph>
            • Phone number is optional and supports international formats
          </Typography>
          <Typography variant="body2" color="textSecondary">
            • All required fields must be filled before submission
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default UserForm;