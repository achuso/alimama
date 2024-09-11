import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Row, Col, Image, Button, Card } from 'react-bootstrap';
import DashboardNavbar from '../DashboardNavbar.tsx';

interface Item {
  _id: string;
  productName: string;
  price: number;
  numInStock: number;
  pictures: string[];
  ratingAvgTotal: number;
}

interface Review {
  reviewerName: string;
  reviewText: string;
  reviewRating: number
}

const ItemDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>(); // Get the item id from the URL
  const [item, setItem] = useState<Item | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);

  useEffect(() => {
    // Fetch item details
    const fetchItemDetails = async () => {
      try {
        const response = await fetch(`/api/items/retrieve/${id}`);
        const data = await response.json();
        setItem(data);
      } 
      catch (error) {
        console.error('Error fetching item details:', error);
      }
    };

    // Fetch the reviews
    const fetchReviews = async () => {
      try {
        const response = await fetch(`/api/items/${id}/reviews`);
        const data = await response.json();
        setReviews(data);
      } 
      catch (error) {
        console.error('Error fetching reviews:', error);
      }
    };

    fetchItemDetails();
    fetchReviews();
  }, [id]);

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
            <p>Price: {item?.price.toFixed(2)} TRY</p>
            <p>In Stock: {item?.numInStock}</p>
            <Button variant="primary" className="mb-4">Add to Cart</Button>

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
