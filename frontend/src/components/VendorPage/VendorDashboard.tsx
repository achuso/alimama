import React, { useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';
import { useItems } from '../../hooks/useItems.tsx';
import ItemModal from './ItemModal.tsx';
import { Item } from '../../types.tsx';
import DashboardNavbar from '../DashboardNavbar.tsx';

const VendorDashboard: React.FC = () => {
  const { items, createItem, updateItem, deleteItem } = useItems();
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState<Partial<Item> | null>(null);

  const handleCreateItem = (newItem: Partial<Item>) => {
    const vendorId = Number(localStorage.getItem('userID'));
    createItem({ ...newItem, vendorId });
    setShowModal(false); // Close the modal after saving
  };

  const handleEditItem = (item: Item) => {
    setEditItem(item); // Set the item to be edited
    setShowModal(true); // Open the modal
  };

  const handleSaveItem = (item: Partial<Item>) => {
    if (editItem) {
      // If there's an editItem, update it
      updateItem({ ...item, _id: editItem._id }); // Ensure the _id is passed to update the correct item
    } else {
      // Else, create a new item
      handleCreateItem(item);
    }
    setEditItem(null);
    setShowModal(false);
  };

  const handleDeleteItem = (id: any) => {
    // Check if ID is an object and has a specific property indicating it’s an ObjectId
    if (typeof id === 'object' && id !== null) {
      id = id.toString(); // Convert object to string if applicable
    }

    if (typeof id === 'string' && id.trim() !== '') {
      console.log('Deleting item with ID:', id);
      deleteItem(id);
    } else {
      console.error('ID should be a valid string:', id);
    }
  };

  return (
    <Container>
      <DashboardNavbar title="Vendor Dashboard" />

      <h2 className="mt-4">Your Items</h2>
      <Button
        variant="primary"
        onClick={() => {
          setEditItem(null); // Clear edit state when creating a new item
          setShowModal(true);
        }}
        className="mb-3"
      >
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
          {items.map((item) => {
            // Ensure item.price is a number and use a default value if not
            const price = typeof item.price === 'number' ? item.price : 0;
            return (
              <tr key={item._id.toString()}>
                <td>{item.productName}</td>
                <td>{item.numInStock}</td>
                <td>{price.toFixed(2)}</td>
                <td>
                  <Button
                    variant="warning"
                    className="mr-2"
                    onClick={() => handleEditItem(item)}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="danger"
                    onClick={() => handleDeleteItem(item._id as string)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </Table>

      <ItemModal
        show={showModal}
        onHide={() => setShowModal(false)}
        onSave={handleSaveItem}
        item={editItem} // pass the item to edit to the modal
      />
    </Container>
  );
};

export default VendorDashboard;
