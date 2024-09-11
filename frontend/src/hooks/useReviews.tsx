// import { useState, useEffect } from 'react';

// export const useReviews = (itemId: string) => {
//   const [reviews, setReviews] = useState<Review[]>([]);
//   const [reviewsChanged, setReviewsChanged] = useState(false);

//   const fetchReviews = async () => {
//     try {
//       const response = await fetch(`/api/items/${itemId}/reviews`);
//       const data = await response.json();
//       setReviews(data);
//       setReviewsChanged(true);
//     } 
//     catch (error) {
//       console.error('Error fetching reviews:', error);
//     }
//   };

//   useEffect(() => {
//     if (itemId)
//       fetchReviews();
//   }, [itemId, reviewsChanged]);

//   return { reviews };
// };