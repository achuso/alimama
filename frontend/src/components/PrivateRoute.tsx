import React, { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  exp: number;
}

interface PrivateRouteProps {
  children: ReactNode;
}

const isTokenExpired = (token: string): boolean => {
  const decoded: JwtPayload = jwtDecode(token);
  const currentTime = Math.floor(Date.now() / 1000);
  return decoded.exp < currentTime;
};

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const authToken = localStorage.getItem('authToken');

  if (!authToken || isTokenExpired(authToken)) {
    // remove tokens if expired
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userFullName');
    return <Navigate to="/login" />;
  }

  return <>{children}</>;
};

export default PrivateRoute;
