import React, { useState } from 'react';
import LoginForm from './LoginForm.tsx';
import RegisterForm from './RegisterForm.tsx';

const AuthContainer: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [role, setRole] = useState('Customer'); // default role is customer

  const toggleForm = () => {
    setIsLogin(!isLogin);
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div 
            className="card shadow-strong bg-white text-dark border-0 rounded-3"
            style={{ transition: 'transform 0.2s', cursor: 'pointer', boxShadow: '0 4px 8px rgba(0,0,0,0.1), 0 6px 20px rgba(0,0,0,0.19)' }}
            onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.05)')}
            onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
          >
            <div className="card-body p-5">
              <h1 className="card-title text-center mb-4">Welcome to Alimama!</h1>

              {/* Conditionally render role selection (not if logging in) */}
              {!isLogin && (
                <div className="text-center mb-4">
                  <div className="btn-group" role="group" aria-label="Role selection">
                    <button type="button" className={`btn ${role === 'Customer' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setRole('Customer')}>
                      <i className="bi bi-person-circle me-2"></i>Customer
                    </button>
                    <button type="button" className={`btn ${role === 'Vendor' ? 'btn-success' : 'btn-outline-success'}`} onClick={() => setRole('Vendor')}>
                      <i className="bi bi-shop-window me-2"></i>Vendor
                    </button>
                    <button type="button" className={`btn ${role === 'Admin' ? 'btn-danger' : 'btn-outline-danger'}`} onClick={() => setRole('Admin')}>
                      <i className="bi bi-shield-lock-fill me-2"></i>Admin
                    </button>
                  </div>
                </div>
              )}

              {isLogin ? <LoginForm /> : <RegisterForm role={role} />}
              
              <button
                className="btn btn-link btn-block text-decoration-none"
                onClick={toggleForm}
              >
                {isLogin ? "Don't have an account? Register here!" : 'Already have an account? Login here!'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthContainer;