import { useState, useEffect } from 'react';
import { Item } from '../types.tsx';

export const useItems = () => {
  const [items, setItems] = useState<Item[]>([]);

  const fetchItems = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/items/retrieve');
      const data = await response.json();
      setItems(data);
    } 
    catch (error) {
      console.error('Error fetching items:', error);
    }
  };

  const createItem = async (itemData: Partial<Item>) => {
    try {
      const response = await fetch('http://localhost:8080/api/items/insert', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemData),
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
      } 
      else {
        console.error('Error deleting item:', response.statusText);
      }
    } 
    catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  useEffect(() => {
    fetchItems();
  }, []);

  return { items, createItem, deleteItem };
};
