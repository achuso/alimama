import { useState, useEffect } from 'react';
import { Item } from '../types.tsx';
import { jwtDecode } from 'jwt-decode'; // You can use a library like jwt-decode

interface TokenPayload {
  userId: number;
  email: string;
  fullName: string;
  role: string;
}

export const useItems = () => {
  const [items, setItems] = useState<Item[]>([]);

  const getVendorIdFromToken = () => {
    const token = localStorage.getItem('authToken'); // Changed from 'jwtToken' to 'authToken'
    if (token) {
      try {
        const decoded: TokenPayload = jwtDecode(token);
        console.log('Decoded token:', decoded); // Log to inspect the structure
        return decoded.userId;
      } 
      catch (error) {
        console.error('Error decoding token:', error);
      }
    } 
    else {
      console.error('No token found in localStorage');
    }
    return null;
  };
  

  const fetchItems = async () => {
    const vendorId = getVendorIdFromToken();
    if (!vendorId) {
      console.error('No vendorId found in token');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/items/retrieve');
      const data = await response.json();

      // Filter the items by vendorId
      const vendorItems = data.filter((item: Item) => item.vendorId === vendorId);
      setItems(vendorItems);
    } 
    catch (error) {
      console.error('Error fetching items:', error);
    }
  };

  const createItem = async (itemData: Partial<Item>) => {
    const token = localStorage.getItem('authToken'); 
    if (!token) {
      console.error('No token found');
      return;
    }
  
    const decoded: TokenPayload = jwtDecode(token);
    const vendorId = decoded.userId; // Get vendorId from the token
  
    try {
      const response = await fetch('http://localhost:8080/api/items/insert', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ ...itemData, vendorId }), // attach vendorId to the item data
      });
  
      if (response.ok) {
        fetchItems(); // Refresh the list after successful creation
      } 
      else {
        const errorText = await response.text();
        console.error(`Error creating item: ${errorText}`);
      }
    } 
    catch (error) {
      console.error('Error creating item:', error);
    }
  };
  

  const updateItem = async (itemData: Partial<Item>) => {
    try {
      const response = await fetch('http://localhost:8080/api/items/update', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemData),
      });
      if (response.ok) {
        fetchItems(); // Refresh the list after successful update
      } else {
        const errorText = await response.text();
        console.error(`Error updating item: ${errorText}`);
      }
    } catch (error) {
      console.error('Error updating item:', error);
    }
  };

  const deleteItem = async (id: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/items/delete`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ _id: id }),
      });
      if (response.ok) {
        fetchItems();
      } else {
        console.error('Error deleting item:', response.statusText);
      }
    } catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  useEffect(() => {
    fetchItems();
  });

  return { items, createItem, updateItem, deleteItem };
};
