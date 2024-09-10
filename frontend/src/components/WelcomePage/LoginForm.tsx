import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FormInput from './FormInput.tsx';
import { jwtDecode } from 'jwt-decode'; 
import { JwtPayload } from '../../types.tsx';

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const navigate = useNavigate();

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

      // Store login token and decode it
      const token = await response.text(); // Retrieve the token as a string
      localStorage.setItem('authToken', token);

      // Decode the token to extract user information
      const decoded: JwtPayload = jwtDecode(token);
      const { fullName, role, userId } = decoded;

      // Store the user info in localStorage
      localStorage.setItem('userRole', role);
      localStorage.setItem('userFullName', fullName);
      localStorage.setItem('userId', userId.toString());

      setSuccess(`Login successful! Welcome, ${fullName}. Redirecting...`);
      setError(null);

      // Redirect based on role
      setTimeout(() => {
        if (role === 'Vendor')
          navigate('/vendor-dashboard');
        else if(role === 'Customer')
          navigate('/customer-dashboard')
        // Other role-based navigation here
      }, 2000);

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
