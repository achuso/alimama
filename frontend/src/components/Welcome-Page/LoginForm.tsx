import React, { useState } from 'react';
import FormInput from './FormInput.tsx';

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null); // Added success state

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

      if (!response.ok) {
        const data = await response.json();
        setError(data.message || 'Login failed. Please check your credentials and try again.');
        setSuccess(null);
        return;
      }

      const data = await response.json();
      console.log('Login successful:', data);
      setSuccess('Login successful!');
      setError(null);
      
      // further logic here
      // session tokenization
      // redirection

    } 
    catch (error) {
      setError('Login failed. Please check your credentials and try again.');
      setSuccess(null);
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
        {success && <p style={{ color: 'green' }}>{success}</p>} {/* Success message */}
        <button type="submit" className="btn btn-primary w-100">
          Login
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
