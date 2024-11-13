import React, { useState, useEffect } from 'react';
import { useDropzone } from 'react-dropzone';
import axios from 'axios';

function UploadImage() {
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [category, setCategory] = useState('');
  const [subCategory, setSubCategory] = useState('');
  const [categories, setCategories] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [newSubCategoryName, setNewSubCategoryName] = useState('');

  const onDrop = (acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setImage(acceptedFiles[0]);
      setImageName(acceptedFiles[0].name);
      console.log('Accepted file:', acceptedFiles[0]);
    }
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: 'image/*',
  });

  const handleImageNameChange = (event) => {
    setImageName(event.target.value);
    console.log('Image name changed:', event.target.value);
  };

  const handleCategoryChange = (event) => {
    const selectedCategoryId = event.target.value;
    setCategory(selectedCategoryId);
    setSubCategory(''); // Reset sub-category when category changes

    // Fetch sub-categories based on selected category
    if (selectedCategoryId) {
      fetchSubCategories(selectedCategoryId);
    } else {
      setSubCategories([]); // Clear sub-categories if no category is selected
    }
    console.log('Category changed:', selectedCategoryId);
  };

  const handleSubCategoryChange = (event) => {
    setSubCategory(event.target.value);
    console.log('Sub-category changed:', event.target.value);
  };

  const fetchCategories = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/v1/category');
      setCategories(response.data); // Assuming response.data is an array of categories
      console.log('Fetched categories:', response.data);
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const fetchSubCategories = async (categoryId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/v1/category/${categoryId}/subcategories`);
      setSubCategories(response.data); // Assuming response.data is an array of subcategories
      console.log('Fetched sub-categories for category ID:', categoryId, response.data);
    } catch (error) {
      console.error('Error fetching sub-categories:', error);
    }
  };

  const handleAddCategory = async () => {
    const categoryData = {
      name: newCategoryName,
      description: 'Description for ' + newCategoryName,
      iconUrl: '',
      displayOrder: 0,
      level: 0,
      parentId: 0,
      parent: true,
      active: true,
    };

    try {
      const response = await axios.post('http://localhost:8080/api/v1/category', categoryData);
      console.log('Category added successfully:', response.data);
      fetchCategories(); // Refresh categories after adding
      setNewCategoryName(''); // Clear input
    } catch (error) {
      console.error('Error adding category:', error);
    }
  };

  const handleAddSubCategory = async () => {
    const subCategoryData = {
      name: newSubCategoryName,
      description: 'Description for ' + newSubCategoryName,
      iconUrl: '',
      displayOrder: 0,
      level: 1,
      parentId: category, // Use selected category ID as parentId
      parent: false,
      active: true,
    };

    try {
      const response = await axios.post('http://localhost:8080/api/v1/category', subCategoryData);
      console.log('Sub-category added successfully:', response.data);
      fetchSubCategories(category); // Refresh sub-categories after adding
      setNewSubCategoryName(''); // Clear input
    } catch (error) {
      console.error('Error adding sub-category:', error);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    console.log('Form submitted');

    // Create a FormData object to hold the data
    const formData = new FormData();
    formData.append('image', image);

    // Store the image name and file temporarily in session storage
    sessionStorage.setItem('uploadedImage', imageName);
    sessionStorage.setItem('uploadedImageFile', image);

    try {
      const response = await axios.post('http://localhost:8080/api/v1/file/upload', {
        file: imageName,
      });

      console.log('Upload successful:', response);
      console.log('Response Data:', response.data);
      console.log('Response Status:', response.status);
      console.log('Response Headers:', response.headers);
    } catch (error) {
      console.error('Error uploading the image:', error.response ? error.response.data : error.message);
    }
  };

  useEffect(() => {
    fetchCategories(); // Fetch categories on component mount
  }, []);

  return (
    <div className='UploadImage w-full h-full flex justify-center items-center'>
      <div className="max-w-md mx-auto p-4 bg-white text-black shadow-md rounded-lg">
        <h2 className="text-lg font-semibold mb-4">Upload Image</h2>
        <form onSubmit={handleSubmit}>
          <div {...getRootProps()} className={`mb-4 border-2 border-dashed rounded-md p-4 cursor-pointer ${isDragActive ? 'border-blue-500' : 'border-gray-300'}`}>
            <input {...getInputProps()} />
            {
              isDragActive ? (
                <p className="text-gray-600">Drop the files here ...</p>
              ) : (
                <p className="text-gray-600">Drop image files here, or click to select files</p>
              )
            }
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">Image Name</label>
            <input
              type="text"
              value={imageName}
              onChange={handleImageNameChange}
              className="mt-1 block w-full border border-gray-300 text-gray-700 rounded-md p-2"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">Category</label>
            <select
              value={category}
              onChange={handleCategoryChange}
              className="mt-1 block w-full border border-gray-300 text-gray-700 rounded-md p-2 cursor-pointer"
              required
            >
              <option value="">Select Category</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
            <input
              type="text"
              value={newCategoryName}
              onChange={(e) => setNewCategoryName(e.target.value)}
              placeholder="New Category Name"
              className="mt-2 block w-full border border-gray-300 text-gray-700 rounded-md p-2"
            />
            <button type="button" onClick={handleAddCategory} className="mt-2 w-full bg-green-500 text-white font-semibold py-2 rounded-md hover:bg-green-600">
              Add Category
            </button>
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">Sub-category</label>
            <select
              value={subCategory}
              onChange={handleSubCategoryChange}
              className="mt-1 block w-full border border-gray-300 text-gray-700 rounded-md p-2 cursor-pointer"
              disabled={!category} // Disable until a category is selected
              required
            >
              <option value="">Select Sub-category</option>
              {subCategories.map((subCat) => (
                <option key={subCat.id} value={subCat.name}>{subCat.name}</option>
              ))}
            </select>
            <input
              type="text"
              value={newSubCategoryName}
              onChange={(e) => setNewSubCategoryName(e.target.value)}
              placeholder="New Sub-category Name"
              className="mt-2 block w-full border border-gray-300 text-gray-700 rounded-md p-2"
              disabled={!category} // Disable input until a category is selected
            />
            <button type="button" onClick={handleAddSubCategory} className="mt-2 w-full bg-green-500 text-white font-semibold py-2 rounded-md hover:bg-green-600" disabled={!category}>
              Add Sub-category
            </button>
          </div>

          <button
            type="submit"
            className="w-full bg-blue-500 text-white font-semibold py-2 rounded-md hover:bg-blue-600"
          >
            Upload
          </button>
        </form>
      </div>
    </div>
  );
}

export default UploadImage;