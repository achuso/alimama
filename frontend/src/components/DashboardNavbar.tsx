import React, { useEffect, useState } from 'react';
import { Navbar, Nav } from 'react-bootstrap';
import CartDropdown from './CustomerPage/CartDropdown.tsx';
import { Item } from '../types.tsx';

interface DashboardNavbarProps {
  title: string;
}

const DashboardNavbar: React.FC<DashboardNavbarProps> = ({ title }) => {
  const [userRole, setUserRole] = useState<string | null>(null);
  const [cartItems, setCartItems] = useState<Item[]>([]);

  useEffect(() => {
    const storedUserRole = localStorage.getItem('userRole');
    if (storedUserRole) {
      setUserRole(storedUserRole);
    }

    // Fetch cart items from MongoDB
    const fetchCartItems = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/cart');
        const data = await response.json();
        setCartItems(data);
      } catch (error) {
        console.error('Error fetching cart items:', error);
      }
    };

    if (userRole === 'Customer') {
      fetchCartItems();
    }
  }, [userRole]);

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="rounded mb-4 p-3">
      <Navbar.Brand className="fw-bold">{title}</Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="me-auto">
          <Nav.Link href="/account">Account</Nav.Link>
          <Nav.Link href="/logout">Logout</Nav.Link>
        </Nav>
        {userRole === 'Customer' && (
          <div className="ms-auto">
            <CartDropdown items={cartItems} />
          </div>
        )}
      </Navbar.Collapse>
    </Navbar>
  );
};

export default DashboardNavbar;