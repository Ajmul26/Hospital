import axios from 'axios';

// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('Response error:', error);
    if (error.response) {
      // Server responded with error status
      console.error('Error data:', error.response.data);
      console.error('Error status:', error.response.status);
    } else if (error.request) {
      // Request was made but no response received
      console.error('No response received:', error.request);
    } else {
      // Something else happened
      console.error('Error message:', error.message);
    }
    return Promise.reject(error);
  }
);

// User data types
export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  phone?: string;
}

export interface UpdateUserRequest {
  name: string;
  email: string;
  phone?: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
}

export interface ErrorResponse {
  error: string;
  message?: string;
  timestamp?: string;
  status?: number;
}

// User API endpoints
export const userApi = {
  // Get all users
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/users');
    return response.data;
  },

  // Get user by ID
  getUserById: async (id: number): Promise<User> => {
    const response = await api.get<User>(`/users/${id}`);
    return response.data;
  },

  // Create new user
  createUser: async (userData: CreateUserRequest): Promise<User> => {
    const response = await api.post<User>('/users', userData);
    return response.data;
  },

  // Update user
  updateUser: async (id: number, userData: UpdateUserRequest): Promise<User> => {
    const response = await api.put<User>(`/users/${id}`, userData);
    return response.data;
  },

  // Delete user
  deleteUser: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },

  // Search users
  searchUsers: async (searchTerm: string): Promise<User[]> => {
    const response = await api.get<User[]>(`/users/search`, {
      params: { term: searchTerm }
    });
    return response.data;
  },

  // Get user by email
  getUserByEmail: async (email: string): Promise<User> => {
    const response = await api.get<User>(`/users/email/${encodeURIComponent(email)}`);
    return response.data;
  },

  // Get users by name containing
  getUsersByName: async (name: string): Promise<User[]> => {
    const response = await api.get<User[]>(`/users/name/${encodeURIComponent(name)}`);
    return response.data;
  },

  // Check if email exists
  checkEmailExists: async (email: string): Promise<boolean> => {
    const response = await api.get<{exists: boolean}>(`/users/exists/email/${encodeURIComponent(email)}`);
    return response.data.exists;
  },

  // Get total user count
  getUserCount: async (): Promise<number> => {
    const response = await api.get<{count: number}>('/users/count');
    return response.data.count;
  },

  // Health check
  healthCheck: async (): Promise<{status: string}> => {
    const response = await api.get<{status: string}>('/users/health');
    return response.data;
  }
};

// Export the configured axios instance for custom requests
export default api;