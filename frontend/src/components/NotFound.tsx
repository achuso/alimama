import React from 'react';
import { useNavigate } from 'react-router-dom';

const NotFound = () => {
  const navigate = useNavigate();

  const handleRedirect = () => {
    navigate('/login');
  };

  return (
    <div className="text-center" style={{ marginTop: '50px' }}>
      <h2>404 - Page Not Found</h2>
      <p>Sorry, the page you are looking for does not exist.</p>
      <button 
        onClick={handleRedirect} 
        className="btn btn-primary mt-3"
      >
        Go to Login Page
      </button>
    </div>
  );
};

export default NotFound;
