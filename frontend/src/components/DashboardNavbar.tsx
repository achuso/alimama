import React from 'react';
import { Navbar, Nav } from 'react-bootstrap';

interface DashboardNavbarProps {
  title: string;
}

const DashboardNavbar: React.FC<DashboardNavbarProps> = ({ title }) => {
  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="rounded mb-4 p-3">
      <Navbar.Brand className="fw-bold">
        {title}
      </Navbar.Brand>
      <Nav className="ml-auto">
        <Nav.Link href="/account">Account</Nav.Link>
        <Nav.Link href="/logout">Logout</Nav.Link>
      </Nav>
    </Navbar>
  );
};

export default DashboardNavbar;
