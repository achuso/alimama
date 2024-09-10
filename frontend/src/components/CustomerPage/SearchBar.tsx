import React from 'react';
import { Form, FormControl, Button } from 'react-bootstrap';

interface SearchBarProps {
  onSearch: (query: string) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ onSearch }) => {
  const [query, setQuery] = React.useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(query);
  };

  return (
    <Form className="d-flex mb-4" onSubmit={handleSearch}>
      <FormControl
        type="search"
        placeholder="Search for items"
        className="me-2"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <Button variant="outline-success" type="submit">
        Search
      </Button>
    </Form>
  );
};

export default SearchBar;
