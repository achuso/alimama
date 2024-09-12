import { useState, useCallback } from 'react';

interface CartItem {
  itemId: string;
  productName: string;
  unitPrice: number;
  quantity: number;
}

interface Cart {
  userId: string;
  items: CartItem[];
  totalAmount: number;
}

export const useCart = () => {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch cart by userId
  const fetchCart = useCallback(async (userId: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8080/api/cart/get/${userId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch cart');
      }
      const data = await response.json();
      setCart(data);
    } catch (err) {
      setError('Error fetching cart');
    } finally {
      setLoading(false);
    }
  }, []);

  // Add item to the cart and reload the page after success
  const addItemToCart = async (userId: string, itemId: string, productName: string, unitPrice: number, quantity: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/cart/add?userId=${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ itemId, productName, unitPrice, quantity }),
      });

      if (response.ok) {
        alert('Item added to cart!');
        window.location.reload();  // Reload the page after adding an item
      } else {
        setError('Error adding item to cart');
      }
    } catch (err) {
      setError('Error adding item to cart');
    }
  };

  // Empty the cart and reload the page after success
  const emptyCart = async (userId: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8080/api/cart/empty`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId }),
      });

      if (response.ok) {
        alert('Cart emptied!');
        window.location.reload();
      } else {
        setError('Error emptying cart');
      }
    } 
    catch (err) {
      setError('Error emptying cart');
    } 
    finally {
      setLoading(false);
    }
  };

  return { cart, loading, error, fetchCart, addItemToCart, emptyCart };
};
