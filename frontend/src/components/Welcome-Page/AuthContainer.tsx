import React, { useState } from 'react';
import LoginForm from './LoginForm.tsx';
import RegisterForm from './RegisterForm.tsx';

const AuthContainer: React.FC = () => {
    const [isLogin, setIsLogin] = useState(true);

    const toggleForm = () => {
        setIsLogin(!isLogin);
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-body">   
                            <h1 className="card-title text-center">
                                Welcome to Alimama!
                            </h1>
                            {isLogin ? <LoginForm /> : <RegisterForm />}
                            <button
                                className="btn btn-link btn-block mt-3"
                                onClick={toggleForm}
                            >
                                {isLogin
                                    ? "Don't have an account? Register here"
                                    : 'Already have an account? Login here'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AuthContainer;
