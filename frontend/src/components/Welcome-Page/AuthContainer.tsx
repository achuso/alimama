
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
                            <h1 className="card-title text-center mb-4" style={{ fontFamily: 'Poppins, sans-serif', fontWeight: 'bold' }}>
                                Welcome to Alimama!
                            </h1>
                            
                            {/* Role selection */}
                            <div className="text-center mb-4">
                                <div className="btn-group" role="group" aria-label="Role selection">
                                    <input type="radio" className="btn-check" name="role" id="roleCustomer" value="Customer"
                                        checked={role === 'Customer'} onChange={() => setRole('Customer')} />
                                    <label className="btn btn-outline-primary btn-lg" htmlFor="roleCustomer">
                                        <i className="bi bi-person-circle me-2"></i>Customer
                                    </label>

                                    <input type="radio" className="btn-check" name="role" id="roleVendor" value="Vendor"
                                        checked={role === 'Vendor'} onChange={() => setRole('Vendor')} />
                                    <label className="btn btn-outline-success btn-lg" htmlFor="roleVendor">
                                        <i className="bi bi-shop-window me-2"></i>Vendor
                                    </label>

                                    <input type="radio" className="btn-check" name="role" id="roleAdmin" value="Admin"
                                        checked={role === 'Admin'} onChange={() => setRole('Admin')} />
                                    <label className="btn btn-outline-danger btn-lg" htmlFor="roleAdmin">
                                        <i className="bi bi-shield-lock-fill me-2"></i>Admin
                                    </label>
                                </div>
                            </div>

                            {isLogin ? <LoginForm role={role} /> : <RegisterForm role={role} />}
                            
                            <button
                                className="btn btn-link btn-block mt-4 text-decoration-none"
                                onClick={toggleForm}
                                style={{ fontSize: '1rem', fontWeight: '600' }}
                            >
                                {isLogin
                                    ? "Don't have an account? Register here!"
                                    : 'Already have an account? Login here!'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AuthContainer;