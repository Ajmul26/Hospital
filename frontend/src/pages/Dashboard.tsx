import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Chip,
  LinearProgress,
  Alert,
  Button,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  DeviceHub as NodesIcon,
  Speed as PerformanceIcon,
  Warning as WarningIcon,
  CheckCircle as SuccessIcon,
  Refresh as RefreshIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  NetworkCheck as NetworkIcon,
} from '@mui/icons-material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
  AreaChart,
  Area,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { apiService } from '../services/apiService';

interface NetworkStats {
  totalNodes: number;
  activeNodes: number;
  inactiveNodes: number;
  overloadedNodes: number;
  maintenanceNodes: number;
  errorNodes: number;
}

interface HealthSummary {
  totalNodes: number;
  healthStatus: string;
  averageLatency: number;
  averageThroughput: number;
  averagePacketLoss: number;
  averageCpuUtilization: number;
  issuesCount: {
    highLatency: number;
    highPacketLoss: number;
    overloaded: number;
  };
}

const Dashboard: React.FC = () => {
  const [networkStats, setNetworkStats] = useState<NetworkStats | null>(null);
  const [healthSummary, setHealthSummary] = useState<HealthSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());

  // Sample data for charts
  const networkTrendData = [
    { time: '00:00', latency: 45, throughput: 85, utilization: 65 },
    { time: '04:00', latency: 52, throughput: 78, utilization: 72 },
    { time: '08:00', latency: 67, throughput: 92, utilization: 85 },
    { time: '12:00', latency: 58, throughput: 88, utilization: 78 },
    { time: '16:00', latency: 71, throughput: 95, utilization: 88 },
    { time: '20:00', latency: 49, throughput: 82, utilization: 69 },
  ];

  const nodeTypeDistribution = [
    { name: 'Routers', value: 35, color: '#1976d2' },
    { name: 'Switches', value: 28, color: '#42a5f5' },
    { name: 'Base Stations', value: 22, color: '#ff9800' },
    { name: 'Gateways', value: 15, color: '#4caf50' },
  ];

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [statsResponse, healthResponse] = await Promise.all([
        apiService.getNetworkStatistics(),
        apiService.getNetworkHealthSummary(),
      ]);
      
      setNetworkStats(statsResponse.data);
      setHealthSummary(healthResponse.data);
      setLastRefresh(new Date());
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
    
    // Refresh data every 30 seconds
    const interval = setInterval(fetchDashboardData, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const getHealthStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'excellent': return 'success';
      case 'good': return 'info';
      case 'fair': return 'warning';
      case 'poor': return 'error';
      default: return 'default';
    }
  };

  const getHealthStatusIcon = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'excellent':
      case 'good':
        return <SuccessIcon />;
      case 'fair':
        return <WarningIcon />;
      case 'poor':
        return <WarningIcon color="error" />;
      default:
        return <NetworkIcon />;
    }
  };

  if (loading && !networkStats) {
    return (
      <Box sx={{ width: '100%', mt: 2 }}>
        <LinearProgress />
        <Typography variant="h6" sx={{ mt: 2, textAlign: 'center' }}>
          Loading network data...
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Network Dashboard
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="body2" color="textSecondary">
            Last updated: {lastRefresh.toLocaleTimeString()}
          </Typography>
          <Tooltip title="Refresh data">
            <IconButton onClick={fetchDashboardData} disabled={loading}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Health Status Alert */}
      {healthSummary && (
        <Alert
          severity={getHealthStatusColor(healthSummary.healthStatus) as any}
          icon={getHealthStatusIcon(healthSummary.healthStatus)}
          sx={{ mb: 3 }}
        >
          <Typography variant="h6">
            Network Health: {healthSummary.healthStatus?.toUpperCase()}
          </Typography>
          <Typography variant="body2">
            {healthSummary.totalNodes} nodes monitored • 
            {healthSummary.issuesCount.overloaded + healthSummary.issuesCount.highLatency + healthSummary.issuesCount.highPacketLoss} issues detected
          </Typography>
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Network Overview Cards */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <NodesIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Total Nodes</Typography>
              </Box>
              <Typography variant="h3" color="primary">
                {networkStats?.totalNodes || 0}
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Active: {networkStats?.activeNodes || 0}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <PerformanceIcon color="success" sx={{ mr: 1 }} />
                <Typography variant="h6">Performance</Typography>
              </Box>
              <Typography variant="h3" color="success.main">
                {healthSummary ? Math.round(100 - healthSummary.averageLatency) : 0}%
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <TrendingUpIcon color="success" fontSize="small" />
                <Typography variant="body2" color="success.main">
                  +2.3% from last hour
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <WarningIcon color="warning" sx={{ mr: 1 }} />
                <Typography variant="h6">Issues</Typography>
              </Box>
              <Typography variant="h3" color="warning.main">
                {healthSummary ? 
                  healthSummary.issuesCount.overloaded + 
                  healthSummary.issuesCount.highLatency + 
                  healthSummary.issuesCount.highPacketLoss : 0}
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Overloaded: {healthSummary?.issuesCount.overloaded || 0}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <NetworkIcon color="info" sx={{ mr: 1 }} />
                <Typography variant="h6">Throughput</Typography>
              </Box>
              <Typography variant="h3" color="info.main">
                {healthSummary ? Math.round(healthSummary.averageThroughput) : 0}
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Mbps average
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Network Performance Trends */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Network Performance Trends (24h)
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={networkTrendData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <RechartsTooltip />
                  <Area
                    type="monotone"
                    dataKey="latency"
                    stackId="1"
                    stroke="#ff9800"
                    fill="#ff9800"
                    fillOpacity={0.3}
                    name="Latency (ms)"
                  />
                  <Area
                    type="monotone"
                    dataKey="throughput"
                    stackId="2"
                    stroke="#1976d2"
                    fill="#1976d2"
                    fillOpacity={0.3}
                    name="Throughput (Mbps)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Node Type Distribution */}
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Node Type Distribution
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={nodeTypeDistribution}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {nodeTypeDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Network Status */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Network Status
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">Active Nodes</Typography>
                    <Typography variant="body2">
                      {networkStats?.activeNodes || 0}/{networkStats?.totalNodes || 0}
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={
                      networkStats?.totalNodes
                        ? (networkStats.activeNodes / networkStats.totalNodes) * 100
                        : 0
                    }
                    color="success"
                  />
                </Box>
                
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">CPU Utilization</Typography>
                    <Typography variant="body2">
                      {healthSummary ? Math.round(healthSummary.averageCpuUtilization) : 0}%
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={healthSummary?.averageCpuUtilization || 0}
                    color={
                      (healthSummary?.averageCpuUtilization || 0) > 80
                        ? 'error'
                        : (healthSummary?.averageCpuUtilization || 0) > 60
                        ? 'warning'
                        : 'success'
                    }
                  />
                </Box>

                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">Packet Loss</Typography>
                    <Typography variant="body2">
                      {healthSummary ? healthSummary.averagePacketLoss.toFixed(2) : 0}%
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={Math.min((healthSummary?.averagePacketLoss || 0) * 10, 100)}
                    color={
                      (healthSummary?.averagePacketLoss || 0) > 5
                        ? 'error'
                        : (healthSummary?.averagePacketLoss || 0) > 2
                        ? 'warning'
                        : 'success'
                    }
                  />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Quick Actions */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Button
                  variant="contained"
                  color="primary"
                  fullWidth
                  startIcon={<TrendingUpIcon />}
                >
                  Run Load Balancing
                </Button>
                <Button
                  variant="outlined"
                  color="secondary"
                  fullWidth
                  startIcon={<PerformanceIcon />}
                >
                  Optimize Performance
                </Button>
                <Button
                  variant="outlined"
                  color="info"
                  fullWidth
                  startIcon={<NetworkIcon />}
                >
                  Network Health Check
                </Button>
              </Box>
              
              <Box sx={{ mt: 3 }}>
                <Typography variant="subtitle2" gutterBottom>
                  System Alerts
                </Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  <Chip
                    size="small"
                    label="3 Nodes Overloaded"
                    color="warning"
                    variant="outlined"
                  />
                  <Chip
                    size="small"
                    label="High Latency Detected"
                    color="error"
                    variant="outlined"
                  />
                  <Chip
                    size="small"
                    label="Optimization Available"
                    color="info"
                    variant="outlined"
                  />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;