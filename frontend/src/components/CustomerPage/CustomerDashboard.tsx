import React, { useEffect, useState } from 'react';
import DashboardNavbar from '../DashboardNavbar.tsx';
import SearchBar from './SearchBar.tsx';
import TagFilter from './TagFilter.tsx';
import ItemList from './ItemList.tsx';
import axios from 'axios';

const CustomerDashboard: React.FC = () => {
  const [items, setItems] = useState<
    {
      _id: string;
      productName: string;
      price: number;
      numInStock: number;
      pictures: string[];
    }[]
  >([]);
  const [tag, setTag] = useState('');

  // Fetch items from MongoDB
  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axios.get('/api/items/retrieve');
        setItems(response.data);
      } catch (error) {
        console.error('Error fetching items:', error);
      }
    };

    fetchItems();
  }, []);

  const handleSearch = (query: string) => {
    console.log('Search query:', query);
    // Search logic
  };

  const handleTagSelect = (selectedTag: string) => {
    setTag(selectedTag);
    console.log('Selected tag:', selectedTag);
    // Tag logic
  };

  return (
    <div className="container mt-4">
      <DashboardNavbar title="E-commerce Site" />
      <SearchBar onSearch={handleSearch} />
      <TagFilter
        tags={['electronics', 'tshirt', 'home', 'kitchen']} // temp tags
        onTagSelect={handleTagSelect}
      />
      <ItemList items={items} />
    </div>
  );
};

export default CustomerDashboard;