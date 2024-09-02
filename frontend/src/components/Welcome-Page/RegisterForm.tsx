import React, { useState } from 'react';
import FormInput from './FormInput.tsx';
import bcrypt from 'bcryptjs';

interface RegisterFormProps {
  role: string;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ role }) => {
  const [legal_name, setLegalName] = useState('');
  const [email, setEmail] = useState('');
  const [tckn, setTckn] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      // Hash the password using bcryptjs
      const salt = bcrypt.genSaltSync(10);
      const hashedPassword = bcrypt.hashSync(password, salt);

      const registrationData = { legal_name, email, tckn, password: hashedPassword, role }

      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(registrationData),
      });

      if (!response.ok) {
        throw new Error('Registration failed');
      }

      const data = await response.json();
      setSuccess('Registration successful!'); // Handle success
      console.log('Registration successful:', data);

    } catch (error) {
      setError('Registration failed. Please check your details and try again.');
    }
  };

  return (
    <div className="register-container">
      <form onSubmit={handleSubmit} className="register-form p-3 mb-4">
        <h2>Register</h2>

        <div className="row">
          <div className="col-md-6">
            {role === 'Customer' || role === 'Admin' ? (
              <FormInput 
                label="Full Name" 
                type="text" 
                id="registerFullName" 
                value={legal_name} 
                onChange={(e) => setLegalName(e.target.value)} 
                required 
              />
            ) : (
              <FormInput 
                label="Company Name" 
                type="text" 
                id="registerCompanyName" 
                value={legal_name} 
                onChange={(e) => setLegalName(e.target.value)} 
                required 
              />
            )}
          </div>
          <div className="col-md-6">
            <FormInput 
              label="Email" 
              type="email" 
              id="registerEmail" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)} 
              required 
            />
          </div>
        </div>

        <div className="row">
          <div className="col-md-6">
            <FormInput 
              label="TCKN" 
              type="text" 
              id="registerTckn" 
              value={tckn} 
              onChange={(e) => setTckn(e.target.value)} 
              required 
            />
          </div>
          <div className="col-md-6">
            <FormInput 
              label="Password" 
              type="password" 
              id="registerPassword" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)} 
              required 
            />
          </div>
        </div>

        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}
        
        <button type="submit" className="btn btn-primary w-100 mt-3">
          Register
        </button>
      </form>
    </div>
  );
};

export default RegisterForm;
