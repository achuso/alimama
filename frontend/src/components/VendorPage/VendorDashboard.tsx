import React, { useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';
import { useItems } from '../../hooks/useItems.tsx';
import ItemModal from './ItemModal.tsx';
import { Item } from '../../types.tsx';
import DashboardNavbar from '../DashboardNavbar.tsx';

const VendorDashboard: React.FC = () => {
  const { items, createItem, deleteItem } = useItems();
  const [showModal, setShowModal] = useState(false);

  const handleCreateItem = (newItem: Partial<Item>) => {
    const vendorId = Number(localStorage.getItem('userID'));
    createItem({ ...newItem, vendorId });
  };

  return (
    <Container>
      {/* Dashboard title as prop */}
      <DashboardNavbar title="Vendor Dashboard" />

      <h2 className="mt-4">Your Items</h2>
      <Button variant="primary" onClick={() => setShowModal(true)} className="mb-3">
        Create New Item
      </Button>

      <Table striped bordered hover className="mt-3">
        <thead>
          <tr>
            <th>Product Name</th>
            <th>In Stock</th>
            <th>Price (TRY)</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item._id}>
              <td>{item.productName}</td>
              <td>{item.numInStock}</td>
              <td>${item.price.toFixed(2)}</td>
              <td>
                <Button variant="warning" className="mr-2">
                  Edit
                </Button>
                <Button variant="danger" onClick={() => deleteItem(item._id)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <ItemModal show={showModal} onHide={() => setShowModal(false)} onSave={handleCreateItem} />
    </Container>
  );
};

export default VendorDashboard;
