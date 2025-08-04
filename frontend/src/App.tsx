import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Box } from '@mui/material';
import AppLayout from './components/layout/AppLayout';
import Dashboard from './pages/Dashboard';
import NetworkNodes from './pages/NetworkNodes';
import Metrics from './pages/Metrics';
import Optimization from './pages/Optimization';
import Analytics from './pages/Analytics';

function App() {
  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/nodes" element={<NetworkNodes />} />
          <Route path="/metrics" element={<Metrics />} />
          <Route path="/optimization" element={<Optimization />} />
          <Route path="/analytics" element={<Analytics />} />
        </Routes>
      </AppLayout>
    </Box>
  );
}

export default App;