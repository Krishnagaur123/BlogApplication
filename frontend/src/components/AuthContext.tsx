// src/components/AuthContext.tsx
import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { apiService, LoginRequest } from '../services/apiService';

interface AuthUser {
  id: string;
  name: string;
  email: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  token: string | null;
}

interface AuthProviderProps {
  children: React.ReactNode;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));

  // Initialize auth state from localStorage token
  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        setToken(storedToken);
        try {
          const userProfile = await apiService.getUserProfile();
          setUser(userProfile);
          setIsAuthenticated(true);
        } catch (err) {
          // Token invalid or expired
          localStorage.removeItem('token');
          setIsAuthenticated(false);
          setUser(null);
          setToken(null);
        }
      }
    };

    initializeAuth();
  }, []);

  // Login
  const login = useCallback(() => {
  setIsAuthenticated(true);
}, []);

  // Register
  const register = useCallback(async (username: string, email: string, password: string) => {
    try {
      await apiService.register({ name:username , email, password });
      // Auto-login after successful registration
      await login();
    } catch (err) {
      throw err;
    }
  }, [login]);

  // Logout
  const logout = useCallback(() => {
    apiService.logout();
    setIsAuthenticated(false);
    setUser(null);
    setToken(null);
  }, []);

  // Update axios header whenever token changes
  useEffect(() => {
    if (token) {
      const axiosInstance = apiService['api'];
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      const axiosInstance = apiService['api'];
      delete axiosInstance.defaults.headers.common['Authorization'];
    }
  }, [token]);

  const value = {
    isAuthenticated,
    user,
    login,
    register,
    logout,
    token
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Hook to use auth
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};

export default AuthContext;
