import {React, useState} from 'react';
import {useNavigate} from 'react-router-dom';

import './Welcome.css';
import Login from './Login.jsx';
import Register from './Register.jsx';

function Welcome(prop) {
    const [isLogin, setIsLogin] = useState(true);

    const toggleForm = () => {
      setIsLogin(!isLogin);
    };
  
    return (
      <div className="welcome-wrapper">
        <div className="form-box">
            mezarsepeti hoşgeldinis
          {isLogin ? <Login /> : <Register />}
          <button className="toggle-button" onClick={toggleForm}>
            {isLogin ? 'Don\'t have an account?' : 'Have an account?'}
          </button>
        </div>
      </div>
    );
};

export default Welcome;