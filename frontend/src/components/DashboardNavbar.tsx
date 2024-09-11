import React, { useEffect, useState } from 'react';
import { Navbar, Nav } from 'react-bootstrap';
import CartDropdown from './CustomerPage/CartDropdown.tsx';

interface DashboardNavbarProps {
  title: string;
}

const DashboardNavbar: React.FC<DashboardNavbarProps> = ({ title }) => {
  const [userRole, setUserRole] = useState<string | null>(null);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const storedUserRole = localStorage.getItem('userRole');
    const storedUserId = localStorage.getItem('userId');
    
    console.log("UserRole:", storedUserRole); 
    console.log("UserId:", storedUserId); 

    if (storedUserRole) {
      setUserRole(storedUserRole);
    }
    if (storedUserId) {
      setUserId(storedUserId);
    }
  }, []);

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="rounded mb-4 p-3">
      <Navbar.Brand className="fw-bold">{title}</Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="me-auto">
          <Nav.Link href="/account">Account</Nav.Link>
          <Nav.Link href="/logout">Logout</Nav.Link>
        </Nav>
        {userRole === 'Customer' && userId && (
          <div className="ms-auto">
            <CartDropdown userId={userId} />
          </div>
        )}
      </Navbar.Collapse>
    </Navbar>
  );
};

export default DashboardNavbar;
