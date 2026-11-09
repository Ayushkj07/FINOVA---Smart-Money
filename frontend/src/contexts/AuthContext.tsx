import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthResponse } from '../types';
import { authApi, googleApi } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  isAdmin: boolean;
  login: (data: any) => Promise<AuthResponse>;
  register: (data: any) => Promise<AuthResponse>;
  googleLogin: (data: { idToken?: string; code?: string; collegeName?: string; currency?: string }) => Promise<AuthResponse>;
  logout: () => void;
  updateProfile: (data: any) => Promise<User>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('finova_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('finova_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        try {
          const res = await authApi.getProfile();
          setUser(res.data);
          localStorage.setItem('finova_user', JSON.stringify(res.data));
        } catch (err) {
          // Keep cached user if offline or network glitch
        }
      }
      setIsLoading(false);
    };
    initAuth();
  }, [token]);

  const login = async (data: any): Promise<AuthResponse> => {
    const res = await authApi.login(data);
    const authData = res.data;
    setToken(authData.token);
    localStorage.setItem('finova_token', authData.token);
    localStorage.setItem('finova_refresh_token', authData.refreshToken);

    const userObj: User = {
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      roles: authData.roles,
      collegeName: authData.collegeName,
      currency: authData.currency || 'INR',
      studentStatus: authData.studentStatus,
    };

    setUser(userObj);
    localStorage.setItem('finova_user', JSON.stringify(userObj));
    return authData;
  };

  const register = async (data: any): Promise<AuthResponse> => {
    const res = await authApi.register(data);
    const authData = res.data;
    setToken(authData.token);
    localStorage.setItem('finova_token', authData.token);
    localStorage.setItem('finova_refresh_token', authData.refreshToken);

    const userObj: User = {
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      roles: authData.roles,
      collegeName: authData.collegeName,
      currency: authData.currency || 'INR',
      studentStatus: authData.studentStatus,
    };

    setUser(userObj);
    localStorage.setItem('finova_user', JSON.stringify(userObj));
    return authData;
  };

  const googleLogin = async (data: { idToken?: string; code?: string; collegeName?: string; currency?: string }): Promise<AuthResponse> => {
    const res = await googleApi.googleAuth(data);
    const authData = res.data;
    setToken(authData.token);
    localStorage.setItem('finova_token', authData.token);
    localStorage.setItem('finova_refresh_token', authData.refreshToken);

    const userObj: User = {
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      roles: authData.roles,
      collegeName: authData.collegeName,
      currency: authData.currency || 'INR',
      studentStatus: authData.studentStatus,
    };

    setUser(userObj);
    localStorage.setItem('finova_user', JSON.stringify(userObj));
    return authData;
  };

  const logout = () => {
    authApi.logout().catch(() => {});
    setToken(null);
    setUser(null);
    localStorage.removeItem('finova_token');
    localStorage.removeItem('finova_refresh_token');
    localStorage.removeItem('finova_user');
  };

  const updateProfile = async (data: any): Promise<User> => {
    const res = await authApi.updateProfile(data);
    setUser(res.data);
    localStorage.setItem('finova_user', JSON.stringify(res.data));
    return res.data;
  };

  const isAdmin = Boolean(user?.roles?.includes('ROLE_ADMIN'));

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isLoading,
        isAdmin,
        login,
        register,
        googleLogin,
        logout,
        updateProfile,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
