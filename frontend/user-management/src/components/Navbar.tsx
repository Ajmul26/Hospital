import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Code as CodeIcon,
} from '@mui/icons-material';
import { Link, useLocation } from 'react-router-dom';

const Navbar: React.FC = () => {
  const location = useLocation();

  const isActive = (path: string) => {
    if (path === '/' && location.pathname === '/') return true;
    if (path !== '/' && location.pathname.startsWith(path)) return true;
    return false;
  };

  const navItems = [
    { path: '/', label: 'Dashboard', icon: <DashboardIcon /> },
    { path: '/users', label: 'Users', icon: <PeopleIcon /> },
    { path: '/users/new', label: 'Add User', icon: <PersonAddIcon /> },
  ];

  return (
    <AppBar position="static" elevation={1}>
      <Toolbar>
        {/* Logo and App Title */}
        <IconButton
          edge="start"
          color="inherit"
          aria-label="logo"
          component={Link}
          to="/"
          sx={{ mr: 2 }}
        >
          <CodeIcon />
        </IconButton>
        
        <Typography 
          variant="h6" 
          component="div" 
          sx={{ 
            flexGrow: 1,
            fontWeight: 600,
            textDecoration: 'none',
            color: 'inherit'
          }}
        >
          User Management System
        </Typography>

        {/* Navigation Links */}
        <Box sx={{ display: 'flex', gap: 1 }}>
          {navItems.map((item) => (
            <Button
              key={item.path}
              color="inherit"
              component={Link}
              to={item.path}
              startIcon={item.icon}
              sx={{
                borderRadius: 2,
                px: 2,
                py: 1,
                backgroundColor: isActive(item.path) ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
                '&:hover': {
                  backgroundColor: 'rgba(255, 255, 255, 0.1)',
                },
                textTransform: 'none',
                fontWeight: isActive(item.path) ? 600 : 400,
              }}
            >
              {item.label}
            </Button>
          ))}
        </Box>

        {/* API Documentation Link */}
        <Button
          color="inherit"
          href="http://localhost:8080/swagger-ui/index.html"
          target="_blank"
          rel="noopener noreferrer"
          sx={{
            ml: 2,
            borderRadius: 2,
            px: 2,
            py: 1,
            border: '1px solid rgba(255, 255, 255, 0.3)',
            '&:hover': {
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
              borderColor: 'rgba(255, 255, 255, 0.5)',
            },
            textTransform: 'none',
          }}
        >
          API Docs
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;