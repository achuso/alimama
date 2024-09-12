import React, { useEffect } from 'react';
import { Dropdown, ButtonGroup, Button } from 'react-bootstrap';
import { useCart } from '../../hooks/useCart.tsx';

interface CartDropdownProps {
  userId: string;
}

const CartDropdown: React.FC<CartDropdownProps> = ({ userId }) => {
  const { cart, loading, error, fetchCart, emptyCart } = useCart();

  useEffect(() => {
    console.log("Fetching cart for user:", userId); // Debugging
    fetchCart(userId);  // Fetch cart for the specific user
  }, [userId, fetchCart]);

  if (loading) {
    return <p>Loading cart...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  console.log("Cart Data:", cart); // Debugging purpose

  return (
    <Dropdown as={ButtonGroup}>
      <Button variant="primary">Cart</Button>
      <Dropdown.Toggle split variant="primary" id="dropdown-split-basic" />
      <Dropdown.Menu align="end">
        {cart && cart.items.length > 0 ? (
          <>
            {cart.items.map((item, index) => (
              <Dropdown.Item key={index}>
                {item.productName} - {item.unitPrice} TRY (x{item.quantity})
              </Dropdown.Item>
            ))}
            <Dropdown.Divider />
            <Dropdown.Item>
              <strong>Total: {cart.totalAmount} TRY</strong>
            </Dropdown.Item>
            <Dropdown.Item onClick={() => emptyCart(userId)}>Empty Cart</Dropdown.Item>
          </>
        ) : (
          <Dropdown.Item disabled>No items in the cart</Dropdown.Item>
        )}
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default CartDropdown;
