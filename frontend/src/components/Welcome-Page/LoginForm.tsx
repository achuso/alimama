import React, { useState } from 'react';

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // login logic here
    // console.log({ email, password });
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form p-3 mb-4">
        <h2>Login</h2>
        <div className="form-group mb-2">
          <label htmlFor="loginEmail" className="col-form-label">
            Email
          </label>
          <input
            type="email"
            className="form-control"
            id="loginEmail"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group mb-2">
          <label htmlFor="loginPassword" className="col-form-label">
            Password
          </label>
          <input
            type="password"
            className="form-control"
            id="loginPassword"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary w-100">
          Login
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
