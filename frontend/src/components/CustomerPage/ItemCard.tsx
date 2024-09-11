import React from 'react';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

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
  
  // Placeholder image
  const defaultImage = 'https://via.placeholder.com/150?text=No+Image';
  const itemImage = item.pictures.length > 0 ? item.pictures[0] : defaultImage;

  // Ensure price is a number and default to 0 if not
  const price = typeof item.price === 'number' ? item.price.toFixed(2) : '0.00';

  const handleCardClick = () => {
    navigate(`/item/${item._id}`);
  };

  return (
    <Card className="mb-4" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
      <Card.Img variant="top" src={itemImage} alt={item.productName} />
      <Card.Body>
        <Card.Title>{item.productName}</Card.Title>
        <Card.Text>
          Price: {price} TRY <br />
          In Stock: {item.numInStock}
        </Card.Text>
        <Button variant="primary">Add to Cart</Button>
      </Card.Body>
    </Card>
  );
};

export default ItemCard;