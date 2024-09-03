import React from 'react';
import { Navigate } from 'react-router-dom';

// for pages that require authentication (e.g. dashboard)
const PrivateRoute = ({ children }) => {
  const authToken = localStorage.getItem('authToken');
  return authToken ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
