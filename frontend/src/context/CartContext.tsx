import React, { createContext, useContext, useState, useEffect } from 'react';
import { useCart } from '../hooks/useCart';

interface CartContextProps {
  cart: any;
  loading: boolean;
  error: string | null;
  refreshCart: () => void;
}

const CartContext = createContext<CartContextProps | undefined>(undefined);

export const CartProvider: React.FC<{ userId: string, children: React.ReactNode }> = ({ userId, children }) => {
  const { cart, loading, error, fetchCart } = useCart();
  const [cartState, setCartState] = useState(cart);

  useEffect(() => {
    fetchCart(userId).then((fetchedCart) => {
      setCartState(fetchedCart);
    });
  }, [userId, fetchCart]);

  const refreshCart = () => {
    fetchCart(userId).then((fetchedCart) => {
      setCartState(fetchedCart);
    });
  };

  return (
    <CartContext.Provider value={{ cart: cartState, loading, error, refreshCart }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCartContext = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCartContext must be used within a CartProvider');
  }
  return context;
};