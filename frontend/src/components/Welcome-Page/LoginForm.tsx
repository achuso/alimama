import React, { useState } from 'react';
import FormInput from './FormInput.tsx';

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: email,
          password: password,
        }),
      });

      if (!response.ok)
        throw new Error('Login failed');

      const data = await response.json();
      // success logic here later
      // save login token, redirect to main page, etc.
      console.log('Login successful:', data);
    } 
    catch (error) {
      setError('Login failed. Please check your credentials and try again.');
    }
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
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <button type="submit" className="btn btn-primary w-100">
          Login
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
