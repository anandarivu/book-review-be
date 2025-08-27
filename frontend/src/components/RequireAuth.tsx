import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

const RequireAuth: React.FC<{ children: React.ReactNode, roles?: string[] }> = ({ children, roles }) => {
  const { token, role } = useAuthStore();
  const jwt = localStorage.getItem('token');

  if (!token || !jwt) {
    return <Navigate to="/login" replace />;
  }
  if (roles && role && !roles.includes(role)) {
    return <Navigate to="/books" replace />;
  }
  return <>{children}</>;
};

export default RequireAuth;
