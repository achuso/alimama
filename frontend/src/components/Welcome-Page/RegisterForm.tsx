import React, { useState } from 'react';

const RegisterForm: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [tckn, setTckn] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // registration logic here
    // console.log({ fullName, email, tckn, password });
  };

  return (
    <div className="register-container">
      <form onSubmit={handleSubmit} className="register-form p-3 mb-4">
        <h2>Register</h2>
        <div className="row g-3 mb-2">
          <div className="col-md-6">
            <label htmlFor="registerFullName" className="col-form-label">
              Full Name
            </label>
            <input
              type="text"
              className="form-control"
              id="registerFullName"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="registerEmail" className="col-form-label">
              Email
            </label>
            <input
              type="email"
              className="form-control"
              id="registerEmail"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
        </div>
        <div className="row g-3 mb-2">
          <div className="col-md-6">
            <label htmlFor="registerTckn" className="col-form-label">
              TCKN
            </label>
            <input
              type="text"
              className="form-control"
              id="registerTckn"
              value={tckn}
              onChange={(e) => setTckn(e.target.value)}
              required
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="registerPassword" className="col-form-label">
              Password
            </label>
            <input
              type="password"
              className="form-control"
              id="registerPassword"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
        </div>
        <button type="submit" className="btn btn-primary w-100">
          Register
        </button>
      </form>
    </div>
  );
};

export default RegisterForm;
