import React, { useState } from 'react';
import FormInput from './FormInput.tsx';

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    //console.log({ email, password });
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form p-3 mb-4">
        <h2>Login</h2>
        <FormInput 
          label="Email" 
          type="email" 
          id="loginEmail" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          required 
        />
        <FormInput 
          label="Password" 
          type="password" 
          id="loginPassword" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)} 
          required 
        />
        <button type="submit" className="btn btn-primary w-100">
          Login
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
