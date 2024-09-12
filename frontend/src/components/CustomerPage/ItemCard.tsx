import React from 'react';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart.tsx';

interface ItemCardProps {
  item: {
    _id: string;
    productName: string;
    price: number | null;
    numInStock: number;
    pictures: string[];
  };
}

const ItemCard: React.FC<ItemCardProps> = ({ item }) => {
  const navigate = useNavigate();
  const { addItemToCart } = useCart();
  const userId = localStorage.getItem('userId');

  const defaultImage = 'https://via.placeholder.com/150?text=No+Image';
  const itemImage = item.pictures.length > 0 ? item.pictures[0] : defaultImage;

  const price = typeof item.price === 'number' ? item.price.toFixed(2) : '0.00';

  const handleAddToCart = () => {
    if (!userId) {
      console.error('User ID is missing');
      return;
    }
    addItemToCart(userId, item._id, item.productName, item.price ?? 0, 1); // Add 1 item by default
  };

  const handleCardClick = () => {
    navigate(`/item/${item._id}`);
  };

  return (
    <Card className="mb-4" style={{ cursor: 'pointer' }}>
      <Card.Img variant="top" src={itemImage} alt={item.productName} onClick={handleCardClick} />
      <Card.Body>
        <Card.Title>{item.productName}</Card.Title>
        <Card.Text>
          Price: {price} TRY <br />
          In Stock: {item.numInStock}
        </Card.Text>
        <Button variant="primary" onClick={handleAddToCart}>Add to Cart</Button>
      </Card.Body>
    </Card>
  );
};

export default ItemCard;
