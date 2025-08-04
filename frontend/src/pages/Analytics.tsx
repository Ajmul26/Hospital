import React from 'react';
import { Typography, Card, CardContent } from '@mui/material';

const Analytics: React.FC = () => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h4" gutterBottom>
          Network Analytics
        </Typography>
        <Typography variant="body1">
          This page will contain advanced network analytics and reporting.
        </Typography>
      </CardContent>
    </Card>
  );
};

export default Analytics;