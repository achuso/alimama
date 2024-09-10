import React from 'react';
import { Form } from 'react-bootstrap';

interface TagFilterProps {
  tags: string[];
  onTagSelect: (selectedTag: string) => void;
}

const TagFilter: React.FC<TagFilterProps> = ({ tags, onTagSelect }) => {
  const handleTagChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onTagSelect(e.target.value);
  };

  return (
    <Form.Select aria-label="Tag Filter" className="mb-4" onChange={handleTagChange}>
      <option value="">Filter by tag</option>
      {tags.map((tag) => (
        <option key={tag} value={tag}>
          {tag}
        </option>
      ))}
    </Form.Select>
  );
};

export default TagFilter;
