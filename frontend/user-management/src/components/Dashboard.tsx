import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Alert,
  CircularProgress,
  Chip,
  Divider,
} from '@mui/material';
import {
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Refresh as RefreshIcon,
  Analytics as AnalyticsIcon,
  Security as SecurityIcon,
  Api as ApiIcon,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { userApi, User } from '../services/api';
import { toast } from 'react-toastify';

const Dashboard: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [apiHealth, setApiHealth] = useState<boolean | null>(null);

  const loadDashboardData = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const [usersData, healthStatus] = await Promise.allSettled([
        userApi.getAllUsers(),
        userApi.healthCheck()
      ]);

      if (usersData.status === 'fulfilled') {
        setUsers(usersData.value);
      } else {
        throw new Error('Failed to load users');
      }

      setApiHealth(healthStatus.status === 'fulfilled');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load dashboard data';
      setError(errorMessage);
      toast.error(errorMessage);
      setApiHealth(false);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    toast.info('Refreshing dashboard data...');
    loadDashboardData();
  };

  const recentUsers = users
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={50} />
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Dashboard
        </Typography>
        <Button
          variant="outlined"
          startIcon={<RefreshIcon />}
          onClick={handleRefresh}
          disabled={loading}
        >
          Refresh
        </Button>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Statistics Cards */}
      <Box sx={{ 
        display: 'grid', 
        gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(4, 1fr)' }, 
        gap: 3, 
        mb: 4 
      }}>
        {/* Total Users */}
        <Card>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography color="textSecondary" gutterBottom variant="body2">
                  Total Users
                </Typography>
                <Typography variant="h4" component="div">
                  {users.length}
                </Typography>
              </Box>
              <PeopleIcon color="primary" sx={{ fontSize: 40 }} />
            </Box>
          </CardContent>
        </Card>

        {/* Recent Users */}
        <Card>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography color="textSecondary" gutterBottom variant="body2">
                  Recent Users
                </Typography>
                <Typography variant="h4" component="div">
                  {recentUsers.length}
                </Typography>
              </Box>
              <AnalyticsIcon color="success" sx={{ fontSize: 40 }} />
            </Box>
          </CardContent>
        </Card>

        {/* API Status */}
        <Card>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography color="textSecondary" gutterBottom variant="body2">
                  API Status
                </Typography>
                <Chip
                  label={apiHealth ? 'Online' : 'Offline'}
                  color={apiHealth ? 'success' : 'error'}
                  variant="filled"
                />
              </Box>
              <ApiIcon color={apiHealth ? 'success' : 'error'} sx={{ fontSize: 40 }} />
            </Box>
          </CardContent>
        </Card>

        {/* Security */}
        <Card>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography color="textSecondary" gutterBottom variant="body2">
                  Security
                </Typography>
                <Chip
                  label="Secured"
                  color="success"
                  variant="outlined"
                />
              </Box>
              <SecurityIcon color="success" sx={{ fontSize: 40 }} />
            </Box>
          </CardContent>
        </Card>
      </Box>

      {/* Main Content */}
      <Box sx={{ 
        display: 'grid', 
        gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, 
        gap: 3,
        mb: 3
      }}>
        {/* Quick Actions */}
        <Card>
          <CardContent>
            <Typography variant="h6" component="h2" gutterBottom>
              Quick Actions
            </Typography>
            <Box display="flex" flexDirection="column" gap={2}>
              <Button
                variant="contained"
                startIcon={<PersonAddIcon />}
                component={Link}
                to="/users/new"
                fullWidth
              >
                Add New User
              </Button>
              <Button
                variant="outlined"
                startIcon={<PeopleIcon />}
                component={Link}
                to="/users"
                fullWidth
              >
                View All Users
              </Button>
              <Button
                variant="outlined"
                startIcon={<ApiIcon />}
                href="http://localhost:8080/swagger-ui/index.html"
                target="_blank"
                rel="noopener noreferrer"
                fullWidth
              >
                API Documentation
              </Button>
            </Box>
          </CardContent>
        </Card>

        {/* Recent Users */}
        <Card>
          <CardContent>
            <Typography variant="h6" component="h2" gutterBottom>
              Recent Users
            </Typography>
            {recentUsers.length === 0 ? (
              <Typography color="textSecondary">
                No users found. Add some users to get started!
              </Typography>
            ) : (
              <Box>
                {recentUsers.map((user, index) => (
                  <Box key={user.id}>
                    <Box display="flex" justifyContent="space-between" alignItems="center" py={1}>
                      <Box>
                        <Typography variant="body1" fontWeight="medium">
                          {user.name}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          {user.email}
                        </Typography>
                      </Box>
                      <Box textAlign="right">
                        <Typography variant="body2" color="textSecondary">
                          {formatDate(user.createdAt)}
                        </Typography>
                        <Button
                          size="small"
                          component={Link}
                          to={`/users/${user.id}`}
                        >
                          View
                        </Button>
                      </Box>
                    </Box>
                    {index < recentUsers.length - 1 && <Divider />}
                  </Box>
                ))}
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>

      {/* System Information */}
      <Card>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            System Information
          </Typography>
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(4, 1fr)' }, 
            gap: 2 
          }}>
            <Box>
              <Typography variant="body2" color="textSecondary">
                Backend API
              </Typography>
              <Typography variant="body1">
                Spring Boot 3.2.0
              </Typography>
            </Box>
            <Box>
              <Typography variant="body2" color="textSecondary">
                Database
              </Typography>
              <Typography variant="body1">
                H2 In-Memory
              </Typography>
            </Box>
            <Box>
              <Typography variant="body2" color="textSecondary">
                Frontend
              </Typography>
              <Typography variant="body1">
                React 18 + TypeScript
              </Typography>
            </Box>
            <Box>
              <Typography variant="body2" color="textSecondary">
                UI Framework
              </Typography>
              <Typography variant="body1">
                Material-UI v7
              </Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Dashboard;