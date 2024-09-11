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
  const [filteredItems, setFilteredItems] = useState(items);  // State to store filtered items
  const [query, setQuery] = useState('');  // Store the search query
  const [tag, setTag] = useState('');

  // Fetch items from MongoDB
  useEffect(() => {
    const fetchAllItems = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/items/retrieve');
        
        const formattedItems = response.data.map((item: any) => ({
          _id: item._id,
          productName: item.productName,
          price: item.price,       
          numInStock: item.numInStock,
          pictures: item.pictures || [],
        }));
        
        setItems(formattedItems);
        setFilteredItems(formattedItems);  // set filteredItems to the full list initially
      } 
      catch (error) {
        console.error('Error fetching items:', error);
      }
    };

    fetchAllItems();
  }, []);

  const handleSearch = (query: string) => {
    setQuery(query);  // Set query in state

    // Filter the items based on the search query
    const filtered = items.filter(item =>
      item.productName.toLowerCase().includes(query.toLowerCase())  // case-insensitive search
    );
    setFilteredItems(filtered);
  };

  const handleTagSelect = (selectedTag: string) => {
    setTag(selectedTag);
    console.log('Selected tag:', selectedTag);
    // Tag logic
  };

  return (
    <div className="container mt-4">
      <DashboardNavbar title="Customer Dashboard" />
      <SearchBar onSearch={handleSearch} />
      <TagFilter
        tags={['electronics', 'tshirt', 'home', 'kitchen']} // temp tags
        onTagSelect={handleTagSelect}
      />
      <ItemList items={filteredItems} />
    </div>
  );
};

export default CustomerDashboard;
