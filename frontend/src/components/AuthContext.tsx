// src/components/AuthContext.tsx
import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { apiService } from '../services/apiService';

interface AuthUser {
  id: string;
  name: string;
  email: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: AuthUser | null;
  login: () => void;
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
        } catch {
          localStorage.removeItem('token');
          setToken(null);
          setUser(null);
          setIsAuthenticated(false);
        }
      }
    };
    initializeAuth();
  }, []);

  const login = useCallback(() => {
    setIsAuthenticated(true);
  }, []);

  const register = useCallback(async (name: string, email: string, password: string) => {
    try {
      await apiService.register({ name, email, password });
    } catch (err) {
      throw err;
    }
  }, []);

  const logout = useCallback(() => {
    apiService.logout();
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  }, []);

  // Update axios header whenever token changes
  useEffect(() => {
    const axiosInstance = apiService['api'];
    if (token) {
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axiosInstance.defaults.headers.common['Authorization'];
    }
  }, [token]);

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, register, logout, token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};

export default AuthContext;
