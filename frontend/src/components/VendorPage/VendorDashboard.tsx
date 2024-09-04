import React, { useEffect, useState } from 'react';
import { Container, Navbar, Nav, Table, Button, Modal, Form } from 'react-bootstrap';

interface Item {
  _id: string;
  vendorId: string;
  productName: string;
  numInStock: number;
  price: number;
  pictures: string[];
  tags: string[];
  ratingAvgTotal: number;
}

const VendorDashboard: React.FC = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [newItem, setNewItem] = useState<Item>({
    _id: '',
    vendorId: '',
    productName: '',
    numInStock: 0,
    price: 0,
    pictures: [],
    tags: [],
    ratingAvgTotal: 0,
  });

  useEffect(() => {
    fetchItems();
  }, []);

  // Fetch items using the fetch API
  const fetchItems = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/items/retrieve', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      const data = await response.json();
      setItems(data);
    } catch (error) {
      console.error('Error fetching items:', error);
    }
  };

  // Handle form input changes
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNewItem({ ...newItem, [name]: value });
  };

  // Handle file selection
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {

  };

  const handleCreateItem = async () => {
    const vendorId = Number(localStorage.getItem('userID'));
  
    const itemData = {
      vendorId: vendorId,
      productName: newItem.productName,
      numInStock: Number(newItem.numInStock),
      price: Number(newItem.price),
      tags: newItem.tags || [],
      ratingAvgTotal: newItem.ratingAvgTotal || 0
    };
  
    try {
      const response = await fetch('http://localhost:8080/api/items/insert', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemData),
      });
  
      if (!response.ok) {
        const errorText = await response.text();
        console.error(`Error creating item: ${errorText}`);
      } else {
        setShowModal(false);
        fetchItems(); // Refresh the list of items
      }
    } catch (error) {
      console.error('Error creating item:', error);
    }
  };
  
  

  // Delete an item using fetch API
  const handleDeleteItem = async (id: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/items/delete`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ _id: id }),
      });
      if (response.ok) {
        fetchItems();
      } else {
        console.error('Error deleting item:', response.statusText);
      }
    } catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  return (
    <Container>
      <Navbar bg="dark" variant="dark" expand="lg" className="rounded mb-4 p-3">
        <Navbar.Brand href="/dashboard" className="fw-bold">
          Vendor Dashboard
        </Navbar.Brand>
        <Nav className="ml-auto">
          <Nav.Link href="/account">Account</Nav.Link>
          <Nav.Link href="/logout">Logout</Nav.Link>
        </Nav>
      </Navbar>

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
                <Button variant="danger" onClick={() => handleDeleteItem(item._id)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Create New Item</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="productName">
              <Form.Label>Product Name</Form.Label>
              <Form.Control
                type="text"
                name="productName"
                value={newItem.productName}
                onChange={handleInputChange}
              />
            </Form.Group>

            <Form.Group controlId="numInStock">
              <Form.Label>Number in Stock</Form.Label>
              <Form.Control
                type="number"
                name="numInStock"
                value={newItem.numInStock}
                onChange={handleInputChange}
              />
            </Form.Group>

            <Form.Group controlId="price">
              <Form.Label>Price (TRY)</Form.Label>
              <Form.Control
                type="number"
                name="price"
                value={newItem.price}
                onChange={handleInputChange}
              />
            </Form.Group>

            <Form.Group controlId="pictures">
              <Form.Label>Upload Pictures (Max 3)</Form.Label>
              <Form.Control
                type="file"
                name="pictures"
                multiple
                onChange={handleFileChange}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Close
          </Button>
          <Button variant="primary" onClick={handleCreateItem}>
            Create Item
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default VendorDashboard;
