import React, { useState, useCallback, useEffect } from 'react';
import { apiFetch } from '../../utils/api';
import { useAuth } from '../../context/AuthContext';

// Color options with names and hex values (static)
const colorOptions = [
  { name: 'Red', hex: '#FF0000' },
  { name: 'Green', hex: '#00FF00' },
  { name: 'Blue', hex: '#0000FF' },
  { name: 'Yellow', hex: '#FFFF00' },
  { name: 'Purple', hex: '#800080' },
  { name: 'Orange', hex: '#FFA500' },
];

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [availableCategories, setAvailableCategories] = useState([]);
  const [subCategoriesMap, setSubCategoriesMap] = useState({});
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedSubCategory, setSelectedSubCategory] = useState('');
  const [selectedColor, setSelectedColor] = useState('');
  const [type, setType] = useState('');
  const [colorDropdownOpen, setColorDropdownOpen] = useState(false);

  // Fetch categories and subcategories on component mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const categoriesResponse = await apiFetch('localhost:', 'GET', null, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        setAvailableCategories(categoriesResponse.categories);
        setSubCategoriesMap(categoriesResponse.subCategoriesMap); // Assuming your API returns this structure
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, [token]);

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
    setSelectedCategory(value);
    setSelectedSubCategory(''); // Reset subcategory when category changes
  };

  const handleSubCategoryChange = (event) => {
    setSelectedSubCategory(event.target.value);
  };

  const handleColorSelect = (color) => {
    setSelectedColor(color);
    setColorDropdownOpen(false); // Close dropdown after selection
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('image', image);
    formData.append('imageName', imageName);
    formData.append('category', selectedCategory);
    formData.append('subCategory', selectedSubCategory);
    formData.append('color', selectedColor);
    formData.append('type', type);

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
      <form onSubmit={handleSubmit} className='bg-gradient-to-t from-bgPurple to-bgLightPurple p-6 rounded shadow-md w-96'>
        <h2 className='text-xl font-bold mb-4'>Upload Image</h2>

        <div
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onClick={() => document.querySelector('input[type="file"]').click()}
          className='border-2 border-dashed border-gray-100 rounded p-4 mb-4 flex items-center justify-center cursor-pointer'
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
          className='border border-gray-300 rounded text-black p-2 mb-4 w-full'
          required
        />

        {/* Dropdown for Categories */}
        <select
          value={selectedCategory}
          onChange={handleCategoryChange}
          className='border border-gray-300 text-black rounded p-2 mb-4 w-full'
          required
        >
          <option value=''>Select Category</option>
          {availableCategories.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </select>

        {/* Subcategory dropdown */}
        {selectedCategory && (
          <select
            value={selectedSubCategory}
            onChange={handleSubCategoryChange}
            className='border border-gray-300 text-black rounded p-2 mb-4 w-full'
            required
          >
            <option value=''>Select Subcategory</option>
            {subCategoriesMap[selectedCategory]?.map((subCategory) => (
              <option key={subCategory} value={subCategory}>
                {subCategory}
              </option>
            ))}
          </select>
        )}

        {/* Custom Color Selector */}
        <div className='relative mb-4'>
          <button
            type='button'
            onClick={() => setColorDropdownOpen(!colorDropdownOpen)}
            className='border border-gray-300 rounded p-2 w-full text-left flex items-center justify-between'
          >
            {selectedColor ? (
              <span style={{ display: 'flex', alignItems: 'center' }}>
                <span
                  style={{
                    display: 'inline-block',
                    width: '20px',
                    height: '20px',
                    backgroundColor: selectedColor,
                    borderRadius: '50%',
                    marginRight: '8px',
                  }}
                />
                Color Selected
              </span>
            ) : (
              'Select Color'
            )}
            <span>{colorDropdownOpen ? '▲' : '▼'}</span>
          </button>

          {colorDropdownOpen && (
            <div className='absolute z-10 bg-white border border-gray-300 text-black rounded shadow-md mt-1 w-full'>
              {colorOptions.map((color) => (
                <div
                  key={color.name}
                  onClick={() => handleColorSelect(color.hex)}
                  className='flex items-center p-2 cursor-pointer hover:bg-gray-200'
                >
                  <span
                    style={{
                      display: 'inline-block',
                      width: '20px',
                      height: '20px',
                      backgroundColor: color.hex,
                      borderRadius: '50%',
                      marginRight: '8px',
                    }}
                  />
                  {color.name}
                </div>
              ))}
            </div>
          )}
        </div>

        <select
          value={type}
          onChange={(e) => setType(e.target.value)}
          className='border border-gray-300 text-black rounded p-2 mb-4 w-full'
          required
        >
          <option value=''>Select Style</option>
          <option value='type1'>3D</option>
          <option value='type2'>Anime</option>
          <option value='type3'>Cartoon</option>
          <option value='type4'>Character Design</option>
          <option value='type5'>Pixel</option>
        </select>

        <div className='flex justify-between items-center'>
          <button
            type='submit'
            className='bg-bgDarkBlue text-white rounded p-2 w-1/2 mr-2'
          >
            Upload
          </button>
          <button className='bg-blue-800 text-white rounded p-2 w-1/2 ml-2'>Show demo</button>
        </div>
      </form>
    </div>
  );
}

export default UploadImage;