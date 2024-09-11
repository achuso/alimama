import React from 'react';
import { Dropdown, ButtonGroup, Button } from 'react-bootstrap';
import { Item } from '../../types.tsx';

interface CartDropdownProps {
  items: Item[];
}

const CartDropdown: React.FC<CartDropdownProps> = ({ items }) => {
  return (
    <Dropdown as={ButtonGroup}>
      <Button variant="primary">Cart</Button>
      <Dropdown.Toggle split variant="primary" id="dropdown-split-basic" />
      <Dropdown.Menu align="end">
        {items.length > 0 ? (
          items.map((item, index) => (
            <Dropdown.Item key={index}>
              {item.productName} - {item.price} TRY
            </Dropdown.Item>
          ))
        ) : (
          <Dropdown.Item disabled>No items in the cart</Dropdown.Item>
        )}
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default CartDropdown;
