import React from 'react';
import { Card, Button } from 'react-bootstrap';

interface ItemCardProps {
  item: {
    _id: string;
    productName: string;
    price: number;
    numInStock: number;
    pictures: string[];
  };
}

const ItemCard: React.FC<ItemCardProps> = ({ item }) => {
  const defaultImage = 'https://via.placeholder.com/150?text=No+Image'; // Placeholder image URL
  const itemImage = item.pictures.length > 0 ? item.pictures[0] : defaultImage;

  return (
    <Card className="mb-4">
      <Card.Img variant="top" src={itemImage} alt={item.productName} />
      <Card.Body>
        <Card.Title>{item.productName}</Card.Title>
        <Card.Text>
          Price: ${item.price.toFixed(2)} <br />
          In Stock: {item.numInStock}
        </Card.Text>
        <Button variant="primary">Add to Cart</Button>
      </Card.Body>
    </Card>
  );
};

export default ItemCard;