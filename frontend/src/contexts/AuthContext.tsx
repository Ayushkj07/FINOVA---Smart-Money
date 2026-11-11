import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthResponse, GoogleAuthChallengeResponse } from '../types';
import { authApi, googleApi } from '../services/api';

export interface GoogleAuthPayload {
  idToken?: string;
  code?: string;
  collegeName?: string;
  currency?: string;
  email?: string;
  fullName?: string;
  sub?: string;
  picture?: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  isAdmin: boolean;
  login: (data: any) => Promise<AuthResponse>;
  register: (data: any) => Promise<AuthResponse>;
  googleLogin: (data: GoogleAuthPayload) => Promise<GoogleAuthChallengeResponse>;
  verifyLoginOtp: (challengeId: string, otp: string) => Promise<AuthResponse>;
  resendLoginOtp: (challengeId: string) => Promise<GoogleAuthChallengeResponse>;
  logout: () => void;
  updateProfile: (data: any) => Promise<User>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    try {
      const saved = localStorage.getItem('finova_user');
      return saved && saved !== 'undefined' ? JSON.parse(saved) : null;
    } catch {
      localStorage.removeItem('finova_user');
      return null;
    }
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('finova_token'));
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    let isMounted = true;
    const initAuth = async () => {
      if (token) {
        try {
          const res = await authApi.getProfile();
          if (isMounted && res.data) {
            setUser(res.data);
            localStorage.setItem('finova_user', JSON.stringify(res.data));
          }
        } catch (err) {
          // Keep cached user if network glitch or offline
        }
      }
      if (isMounted) {
        setIsLoading(false);
      }
    };
    initAuth();
    return () => {
      isMounted = false;
    };
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

  const googleLogin = async (data: GoogleAuthPayload): Promise<GoogleAuthChallengeResponse> => {
    const res = await googleApi.googleAuth(data);
    return res.data;
  };

  const verifyLoginOtp = async (challengeId: string, otp: string): Promise<AuthResponse> => {
    const res = await googleApi.verifyOtp({ challengeId, otp });
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

  const resendLoginOtp = async (challengeId: string): Promise<GoogleAuthChallengeResponse> => {
    const res = await googleApi.resendOtp({ challengeId });
    return res.data;
  };

  const logout = () => {
    try {
      authApi.logout().catch(() => {});
    } finally {
      setUser(null);
      setToken(null);
      localStorage.removeItem('finova_token');
      localStorage.removeItem('finova_refresh_token');
      localStorage.removeItem('finova_user');
    }
  };

  const updateProfile = async (data: any): Promise<User> => {
    const res = await authApi.updateProfile(data);
    const updated = res.data;
    setUser(updated);
    localStorage.setItem('finova_user', JSON.stringify(updated));
    return updated;
  };

  const isAdmin = Boolean(user?.roles && user.roles.includes('ROLE_ADMIN'));

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
        verifyLoginOtp,
        resendLoginOtp,
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
