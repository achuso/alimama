import React, { useEffect, useState } from 'react';
import { Container, Navbar, Nav, Table, Button, Modal, Form } from 'react-bootstrap';
import axios from 'axios';

interface Item {
  _id: string;
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
    productName: '',
    numInStock: 0,
    price: 0,
    pictures: [],
    tags: [],
    ratingAvgTotal: 0,
  });
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      const response = await axios.get<Item[]>('/api/items');
      setItems(response.data);
    } catch (error) {
      console.error('Error fetching items:', error);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNewItem({ ...newItem, [name]: value });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setSelectedFiles(Array.from(e.target.files));
    }
  };

  const handleCreateItem = async () => {
    const formData = new FormData();
    formData.append('productName', newItem.productName);
    formData.append('numInStock', newItem.numInStock.toString());
    formData.append('price', newItem.price.toString());
    selectedFiles.forEach((file, index) => {
      formData.append('pictures', file);
    });

    try {
      await axios.post('/api/items', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setShowModal(false);
      fetchItems();
    } catch (error) {
      console.error('Error creating item:', error);
    }
  };

  const handleDeleteItem = async (id: string) => {
    try {
      await axios.delete(`/api/items/${id}`);
      fetchItems();
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
            <th>Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map(item => (
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
              <Form.Label>Price</Form.Label>
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
