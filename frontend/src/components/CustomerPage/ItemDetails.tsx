import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Row, Col, Image, Button, Card } from 'react-bootstrap';
import DashboardNavbar from '../DashboardNavbar.tsx';
import { Item, Review } from '../../types.tsx';
import { useItems } from '../../hooks/useItems.tsx';
import { useCart } from '../../hooks/useCart.tsx';

const ItemDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { fetchItemById } = useItems();
  const [item, setItem] = useState<Item | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const { addItemToCart } = useCart();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchDetails = async () => {
      if (!id) {
        console.error('No item ID found in the URL');
        return;
      }

      // Use hook to fetch item details by ID
      const fetchedItem = await fetchItemById(id);

      if (fetchedItem) {
        setItem(fetchedItem);
      } 
      else {
        console.error('Failed to fetch item details');
      }
    };

    fetchDetails();
  }, [id, fetchItemById]);

  const handleAddToCart = () => {
    if (!userId || !item) {
      console.error('User ID or item details are missing');
      return;
    }

    addItemToCart(userId, item._id, item.productName, item.price, 1); // Adding 1 quantity by default
  };

  const placeholderImage = 'https://via.placeholder.com/300?text=No+Image+Available';
  const itemImage = item && item.pictures.length > 0 ? item.pictures[0] : placeholderImage;

  return (
    <div>
      <DashboardNavbar title="Product Details" />
      <Container>
        <Row className="mt-4">
          <Col md={6}>
            <Image src={itemImage} alt={item?.productName} fluid />
          </Col>
          <Col md={6}>
            <h1>{item?.productName}</h1>
            <p className="text-muted">Rating: {item?.ratingAvgTotal ?? 'No rating available'}</p>
            <p>Price: {item?.price} TRY</p>
            <p>In Stock: {item?.numInStock}</p>
            <Button variant="primary" className="mb-4" onClick={handleAddToCart}>Add to Cart</Button>

            <h3>Reviews</h3>
            {reviews.length > 0 ? (
              reviews.map((review, index) => (
                <Card key={index} className="mb-3">
                  <Card.Body>
                    <Card.Title>{review.reviewerName}</Card.Title>
                    <Card.Text>{review.reviewText}</Card.Text>
                  </Card.Body>
                </Card>
              ))
            ) : (
              <p>No reviews yet.</p>
            )}
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default ItemDetails;