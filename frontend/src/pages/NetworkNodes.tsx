import React from 'react';
import { Typography, Card, CardContent } from '@mui/material';

const NetworkNodes: React.FC = () => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h4" gutterBottom>
          Network Nodes Management
        </Typography>
        <Typography variant="body1">
          This page will contain the network nodes management interface.
        </Typography>
      </CardContent>
    </Card>
  );
};

export default NetworkNodes;