import React from 'react';
import { Row, Col } from 'react-bootstrap';
import ItemCard from './ItemCard.tsx';

interface ItemListProps {
  items: {
    _id: string;
    productName: string;
    price: number;
    numInStock: number;
    pictures: string[];
  }[];
}

const ItemList: React.FC<ItemListProps> = ({ items }) => {
  return (
    <Row>
      {items.map((item) => (
        <Col key={item._id} xs={12} sm={6} md={4} lg={3} xl={2}>
          <ItemCard item={item} />
        </Col>
      ))}
    </Row>
  );
};

export default ItemList;