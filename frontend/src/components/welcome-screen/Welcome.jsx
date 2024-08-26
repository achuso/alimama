import React, { useState } from 'react';
import './Welcome.css'

function Welcome() {
  const [isLogin, setIsLogin] = useState(true);
  const [role, setRole] = useState('');

  const toggleForm = () => setIsLogin(!isLogin);

  const handleRoleChange = (event) => setRole(event.target.value);

  return (
    <div className="alimama-container">
      <h1 className="text-center">{isLogin ? 'Login' : 'Register'}</h1>

      <form>
        <div>
          <label htmlFor="email">Email</label>
          <input type="email" id="email" placeholder="Enter your email" required />
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input type="password" id="password" placeholder="Enter your password" required />
        </div>

        {!isLogin && (
          <div>
            <label htmlFor="confirm-password">Confirm Password</label>
            <input type="password" id="confirm-password" placeholder="Confirm your password" required />
          </div>
        )}

        <div className="role-selection">
          <p>Select your role:</p>
          <div className="flex gap-x-4">
            <label>
              <input
                type="radio"
                name="role"
                value="seller"
                checked={role === 'seller'}
                onChange={handleRoleChange}
              />
              Seller
            </label>
            <label>
              <input
                type="radio"
                name="role"
                value="buyer"
                checked={role === 'buyer'}
                onChange={handleRoleChange}
              />
              Buyer
            </label>
            <label>
              <input
                type="radio"
                name="role"
                value="admin"
                checked={role === 'admin'}
                onChange={handleRoleChange}
              />
              Admin
            </label>
          </div>
        </div>

        <button type="submit">{isLogin ? 'Login' : 'Register'}</button>
      </form>

      <button onClick={toggleForm} className="toggle-button">
        {isLogin ? 'Don’t have an account? Register' : 'Already have an account? Login'}
      </button>
    </div>
  );
};

export default Welcome;
