import React from 'react';
import { Typography, Card, CardContent } from '@mui/material';

const Metrics: React.FC = () => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h4" gutterBottom>
          Network Metrics
        </Typography>
        <Typography variant="body1">
          This page will contain network performance metrics and monitoring.
        </Typography>
      </CardContent>
    </Card>
  );
};

export default Metrics;