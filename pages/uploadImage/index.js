import React, { useState, useCallback } from 'react';
import { apiFetch } from '../../utils/api';
import { useAuth } from '../../context/AuthContext';

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [category, setCategory] = useState('');
  const [style, setStyle] = useState('');
  const [tags, setTags] = useState('');

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setImage(file);
    }
  };

  const handleDrop = useCallback((event) => {
    event.preventDefault();
    const file = event.dataTransfer.files[0];
    if (file) {
      setImage(file);
    }
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('image', image);
    formData.append('imageName', imageName);
    formData.append('category', category);
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
        
        {/* ناحیه درگ و دراپ */}
        <div 
          onDrop={handleDrop} 
          onDragOver={handleDragOver} 
          className='border-2 border-dashed border-gray-400 rounded p-4 mb-4 flex items-center justify-center'
        >
          {image ? (
            <p>{image.name}</p>
          ) : (
            <p>Drag & drop your image here or click to select</p>
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

        <select 
          value={category} 
          onChange={(e) => setCategory(e.target.value)} 
          className='border border-gray-300 rounded p-2 mb-4 w-full'
          required
        >
          <option value=''>Select Category</option>
          <option value='category1'>Category 1</option>
          <option value='category2'>Category 2</option>
          <option value='category3'>Category 3</option>
        </select>

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