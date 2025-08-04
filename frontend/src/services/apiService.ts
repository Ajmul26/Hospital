import axios, { AxiosResponse } from 'axios';

// Configure base URL - will use proxy in development
const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Types
export interface NetworkNode {
  id?: string;
  name: string;
  type: string;
  location: {
    latitude: number;
    longitude: number;
    address: string;
    city: string;
    region: string;
    country: string;
  };
  status: string;
  capacity: number;
  currentLoad: number;
  availableBandwidth: number;
  usedBandwidth: number;
  connectedNodes: string[];
  performanceMetrics: Record<string, any>;
  configuration: Record<string, any>;
  createdAt?: string;
  updatedAt?: string;
}

export interface NetworkMetrics {
  id?: string;
  nodeId: string;
  timestamp?: string;
  latency: number;
  throughput: number;
  packetLoss: number;
  jitter: number;
  cpuUtilization: number;
  memoryUtilization: number;
  bandwidthUtilization: number;
  activeConnections: number;
  errorCount: number;
  signalStrength?: number;
  temperature?: number;
  powerConsumption?: number;
  customMetrics?: Record<string, any>;
}

export interface OptimizationTask {
  id?: string;
  name: string;
  description?: string;
  type: string;
  status?: string;
  priority: string;
  initiatedBy?: string;
  targetNodeIds?: string[];
  parameters?: Record<string, any>;
  results?: Record<string, any>;
  algorithm?: string;
  maxIterations?: number;
  convergenceThreshold?: number;
  startTime?: string;
  endTime?: string;
  progressPercentage?: number;
  errorMessage?: string;
  improvementPercentage?: number;
  performanceGains?: Record<string, number>;
  createdAt?: string;
  updatedAt?: string;
}

// API Service class
class ApiService {
  // Network Nodes API
  async getNetworkNodes(page?: number, size?: number): Promise<AxiosResponse<any>> {
    const params = new URLSearchParams();
    if (page !== undefined) params.append('page', page.toString());
    if (size !== undefined) params.append('size', size.toString());
    
    return apiClient.get(`/nodes?${params.toString()}`);
  }

  async getNetworkNode(id: string): Promise<AxiosResponse<NetworkNode>> {
    return apiClient.get(`/nodes/${id}`);
  }

  async createNetworkNode(node: NetworkNode): Promise<AxiosResponse<NetworkNode>> {
    return apiClient.post('/nodes', node);
  }

  async updateNetworkNode(id: string, node: Partial<NetworkNode>): Promise<AxiosResponse<NetworkNode>> {
    return apiClient.put(`/nodes/${id}`, node);
  }

  async deleteNetworkNode(id: string): Promise<AxiosResponse<void>> {
    return apiClient.delete(`/nodes/${id}`);
  }

  async getNodesByType(type: string): Promise<AxiosResponse<NetworkNode[]>> {
    return apiClient.get(`/nodes/type/${type}`);
  }

  async getNodesByStatus(status: string): Promise<AxiosResponse<NetworkNode[]>> {
    return apiClient.get(`/nodes/status/${status}`);
  }

  async getOverloadedNodes(threshold?: number): Promise<AxiosResponse<NetworkNode[]>> {
    const params = threshold ? `?threshold=${threshold}` : '';
    return apiClient.get(`/nodes/overloaded${params}`);
  }

  async getNetworkStatistics(): Promise<AxiosResponse<any>> {
    return apiClient.get('/nodes/statistics');
  }

  async performNetworkHealthCheck(): Promise<AxiosResponse<any>> {
    return apiClient.post('/nodes/health-check');
  }

  // Network Metrics API
  async recordMetrics(metrics: NetworkMetrics): Promise<AxiosResponse<NetworkMetrics>> {
    return apiClient.post('/metrics', metrics);
  }

  async recordBatchMetrics(metricsList: NetworkMetrics[]): Promise<AxiosResponse<NetworkMetrics[]>> {
    return apiClient.post('/metrics/batch', metricsList);
  }

  async getMetricsByNode(nodeId: string): Promise<AxiosResponse<NetworkMetrics[]>> {
    return apiClient.get(`/metrics/node/${nodeId}`);
  }

  async getRecentMetrics(nodeId: string, hours: number = 24): Promise<AxiosResponse<NetworkMetrics[]>> {
    return apiClient.get(`/metrics/node/${nodeId}/recent?hours=${hours}`);
  }

  async getLatestMetrics(): Promise<AxiosResponse<NetworkMetrics[]>> {
    return apiClient.get('/metrics/latest');
  }

  async getHighLatencyNodes(threshold?: number): Promise<AxiosResponse<NetworkMetrics[]>> {
    const params = threshold ? `?threshold=${threshold}` : '';
    return apiClient.get(`/metrics/high-latency${params}`);
  }

  async getHighPacketLossNodes(threshold?: number): Promise<AxiosResponse<NetworkMetrics[]>> {
    const params = threshold ? `?threshold=${threshold}` : '';
    return apiClient.get(`/metrics/high-packet-loss${params}`);
  }

  async getNetworkHealthSummary(): Promise<AxiosResponse<any>> {
    return apiClient.get('/metrics/health-summary');
  }

  async getPerformanceTrends(nodeId: string, days: number = 7): Promise<AxiosResponse<any>> {
    return apiClient.get(`/metrics/node/${nodeId}/trends?days=${days}`);
  }

  async getTopPerformingNodes(limit: number = 10): Promise<AxiosResponse<any[]>> {
    return apiClient.get(`/metrics/top-performers?limit=${limit}`);
  }

  async getWorstPerformingNodes(limit: number = 10): Promise<AxiosResponse<any[]>> {
    return apiClient.get(`/metrics/worst-performers?limit=${limit}`);
  }

  async getQosDistribution(): Promise<AxiosResponse<any>> {
    return apiClient.get('/metrics/qos-distribution');
  }

  // Optimization API
  async createOptimizationTask(task: OptimizationTask): Promise<AxiosResponse<OptimizationTask>> {
    return apiClient.post('/optimization/tasks', task);
  }

  async executeOptimizationTask(taskId: string): Promise<AxiosResponse<any>> {
    return apiClient.post(`/optimization/tasks/${taskId}/execute`);
  }

  async getOptimizationTask(taskId: string): Promise<AxiosResponse<OptimizationTask>> {
    return apiClient.get(`/optimization/tasks/${taskId}`);
  }

  async getTasksByStatus(status: string): Promise<AxiosResponse<OptimizationTask[]>> {
    return apiClient.get(`/optimization/tasks/status/${status}`);
  }

  async getRunningTasks(): Promise<AxiosResponse<OptimizationTask[]>> {
    return apiClient.get('/optimization/tasks/running');
  }

  async cancelOptimizationTask(taskId: string): Promise<AxiosResponse<any>> {
    return apiClient.post(`/optimization/tasks/${taskId}/cancel`);
  }

  // Quick optimization actions
  async optimizeLoadBalancing(nodeIds?: string[], priority: string = 'MEDIUM'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/load-balancing?${params.toString()}`);
  }

  async optimizeBandwidth(nodeIds?: string[], priority: string = 'MEDIUM'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/bandwidth?${params.toString()}`);
  }

  async optimizeRouting(
    nodeIds?: string[], 
    algorithm: string = 'genetic', 
    maxIterations: number = 1000,
    priority: string = 'HIGH'
  ): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('algorithm', algorithm);
    params.append('maxIterations', maxIterations.toString());
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/routing?${params.toString()}`);
  }

  async performCapacityPlanning(nodeIds?: string[], priority: string = 'HIGH'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/capacity-planning?${params.toString()}`);
  }

  async performFaultDetection(nodeIds?: string[], priority: string = 'CRITICAL'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/fault-detection?${params.toString()}`);
  }

  async performPerformanceTuning(nodeIds?: string[], priority: string = 'HIGH'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/performance-tuning?${params.toString()}`);
  }

  async performCostOptimization(nodeIds?: string[], priority: string = 'MEDIUM'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/cost-optimization?${params.toString()}`);
  }

  async performEnergyOptimization(nodeIds?: string[], priority: string = 'MEDIUM'): Promise<AxiosResponse<OptimizationTask>> {
    const params = new URLSearchParams();
    if (nodeIds && nodeIds.length > 0) {
      nodeIds.forEach(id => params.append('nodeIds', id));
    }
    params.append('priority', priority);
    
    return apiClient.post(`/optimization/energy-efficiency?${params.toString()}`);
  }

  // Utility methods
  async ping(): Promise<AxiosResponse<any>> {
    return apiClient.get('/actuator/health');
  }

  setAuthToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  removeAuthToken(): void {
    localStorage.removeItem('authToken');
  }
}

export const apiService = new ApiService();
export default apiService;