import React, { useState } from 'react';
import FormInput from './FormInput.tsx';

interface RegisterFormProps {
  role: string;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ role }) => {
  const [fullName, setFullName] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [email, setEmail] = useState('');
  const [tckn, setTckn] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // if (role === 'Customer') {
    //   console.log({ fullName, email, tckn, password, role });
    // } 
    // else if (role === 'Vendor') {
    //   console.log({ companyName, email, tckn, password, role });
    // }
  };

  return (
    <div className="register-container">
      <form onSubmit={handleSubmit} className="register-form p-3 mb-4">
        <h2>Register</h2>

        <div className="row">
          <div className="col-md-6">
            {role === 'Customer' ? (
              <FormInput 
                label="Full Name" 
                type="text" 
                id="registerFullName" 
                value={fullName} 
                onChange={(e) => setFullName(e.target.value)} 
                required 
              />
            ) : (
              <FormInput 
                label="Company Name" 
                type="text" 
                id="registerCompanyName" 
                value={companyName} 
                onChange={(e) => setCompanyName(e.target.value)} 
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

        <button type="submit" className="btn btn-primary w-100 mt-3">
          Register
        </button>
      </form>
    </div>
  );
};

export default RegisterForm;
