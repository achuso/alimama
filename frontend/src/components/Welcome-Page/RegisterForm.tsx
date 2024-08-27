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
        <form onSubmit={handleSubmit}>
            <div className="form-group">
                <label htmlFor="registerFullName">Full Name</label>
                <input
                    type="text"
                    className="form-control"
                    id="registerFullName"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="registerEmail">Email</label>
                <input
                    type="email"
                    className="form-control"
                    id="registerEmail"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="registerTckn">TCKN</label>
                <input
                    type="text"
                    className="form-control"
                    id="registerTckn"
                    value={tckn}
                    onChange={(e) => setTckn(e.target.value)}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="registerPassword">Password</label>
                <input
                    type="password"
                    className="form-control"
                    id="registerPassword"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>
            <button type="submit" className="btn btn-primary btn-block">
                Register
            </button>
        </form>
    );
};

export default RegisterForm;
