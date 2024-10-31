import React, { useState, useCallback } from 'react';
import { apiFetch } from '../../utils/api';
import { useAuth } from '../../context/AuthContext';

const availableCategories = [
  'category1',
  'category2',
  'category3',
];

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [style, setStyle] = useState('');
  const [tags, setTags] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setImage(file);
    }
  };

  const handleDrop = useCallback((event) => {
    event.preventDefault();
    event.stopPropagation();
    const file = event.dataTransfer.files[0];
    if (file) {
      setImage(file);
    }
  }, []);

  const handleDragOver = useCallback((event) => {
    event.preventDefault();
    event.stopPropagation();
  }, []);

  const handleCategoryChange = (event) => {
    const value = event.target.value;
    setSelectedCategories((prev) => {
      if (prev.includes(value)) {
        return prev.filter((category) => category !== value);
      } else {
        return [...prev, value];
      }
    });
  };

  const handleRemoveCategory = (category) => {
    setSelectedCategories((prev) => prev.filter((c) => c !== category));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('image', image);
    formData.append('imageName', imageName);
    formData.append('categories', JSON.stringify(selectedCategories));
    formData.append('style', style);
    formData.append('tags', tags.split(',').map(tag => tag.trim()));

    try {
      const response = await apiFetch('/api/upload', 'POST', formData, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response) {
        console.log('Image uploaded successfully', response);
      }
    } catch (error) {
      console.error('Failed to upload image:', error);
    }
  };

  return (
    <div className='w-full h-full flex items-center justify-center'>
      <form onSubmit={handleSubmit} className='bg-white p-6 rounded shadow-md w-96'>
        <h2 className='text-xl font-bold mb-4'>Upload Image</h2>

        <div 
          onDrop={handleDrop} 
          onDragOver={handleDragOver}
          onClick={() => document.querySelector('input[type="file"]').click()}
          className='border-2 border-dashed border-gray-400 rounded p-4 mb-4 flex items-center justify-center cursor-pointer'
        >
          {image ? (
            <p>{image.name}</p>
          ) : (
            <p>Drop your image here or click to select</p>
          )}
          <input 
            type='file' 
            accept='image/*' 
            onChange={handleImageChange} 
            className='hidden'
            required
          />
        </div>

        <input 
          type='text' 
          placeholder='Image Name' 
          value={imageName} 
          onChange={(e) => setImageName(e.target.value)} 
          className='border border-gray-300 rounded p-2 mb-4 w-full'
          required
        />

        {/* Dropdown for Categories */}
        <div className='mb-4 relative'>
          <button 
            type='button' 
            onClick={() => setDropdownOpen(!dropdownOpen)} 
            className='border border-gray-300 rounded p-2 w-full text-left'
          >
            {selectedCategories.length > 0 ? `Categories (${selectedCategories.length})` : 'Categories'}
          </button>
          {dropdownOpen && (
            <div className='absolute z-10 bg-white border border-gray-300 rounded shadow-md mt-1 w-full'>
              {availableCategories.map((category) => (
                <label key={category} className='block p-2'>
                  <input 
                    type='checkbox' 
                    value={category} 
                    checked={selectedCategories.includes(category)} 
                    onChange={handleCategoryChange} 
                    className='mr-2'
                  />
                  {category}
                </label>
              ))}
            </div>
          )}
        </div>

        {/* Display selected categories as tags */}
        <div className='mb-4'>
          {selectedCategories.map((category) => (
            <span key={category} className='inline-flex items-center bg-blue-100 text-blue-800 text-sm font-medium mr-2 px-2.5 py-0.5 rounded'>
              {category}
              <button 
                type='button' 
                onClick={() => handleRemoveCategory(category)} 
                className='ml-1 text-blue-500 hover:text-blue-700'
              >
                &times;
              </button>
            </span>
          ))}
        </div>

        <select 
          value={style} 
          onChange={(e) => setStyle(e.target.value)} 
          className='border border-gray-300 rounded p-2 mb-4 w-full'
          required
        >
          <option value=''>Select Style</option>
          <option value='style1'>Style 1</option>
          <option value='style2'>Style 2</option>
          <option value='style3'>Style 3</option>
        </select>

        <input 
          type='text' 
          placeholder='Tags (comma separated)' 
          value={tags} 
          onChange={(e) => setTags(e.target.value)} 
          className='border border-gray-300 rounded p-2 mb-4 w-full'
        />

        <button 
          type='submit' 
          className='bg-blue-500 text-white rounded p-2 w-full'
        >
          Upload
        </button>
      </form>
    </div>
  );
}

export default UploadImage;