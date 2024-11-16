import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Card from '@/components/templates/Card';
import AddCategoryModal from '@/components/templates/AddCategoryModal';
import { useAuth } from '../../context/AuthContext';

function UploadImage() {
  const { token } = useAuth(); // Get the token from the auth context
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [category, setCategory] = useState('');
  const [subCategory, setSubCategory] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [showCard, setShowCard] = useState(false);
  const [uploadedFile, setUploadedFile] = useState(''); // State to store the uploaded file info
  const [cats, setCats] = useState([]); // State to store categories
  const [subCategories, setSubCategories] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false); // State for modal visibility

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type.startsWith('image/')) {
      setImage(file);
      setErrorMessage('');
    } else {
      setErrorMessage('Please select a valid image file.');
    }
  };

  const handleUploadSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage(''); // Reset error message

    if (!image) {
      setErrorMessage('Please upload an image.');
      return;
    }

    setIsLoading(true);
    const formData = new FormData();
    formData.append('file', image);
    formData.append('fileName', imageName);
    formData.append('parentCategoryName', category);
    formData.append('subCategoryNames', subCategory);
    formData.append('dominantColors', null);
    formData.append('style', null);

    try {
      const UPLOAD_BACKEND_URL = 'http://localhost:8080/api/v1/file/upload';
      const response = await axios.post(UPLOAD_BACKEND_URL, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}` // Include the token in the headers
        },
      });

      // Extract the 'file' property from the response body
      const uploadedFileData = response.data.file; // Assuming response body is { "file": "string" }
      setUploadedFile(uploadedFileData); // Store the uploaded file info in state
      alert('Upload successful: ' + uploadedFileData); // Notify the user
    } catch (error) {
      console.error('Error uploading file:', error);
      setErrorMessage(error.response?.data?.message || 'Error uploading file.');
    } finally {
      setIsLoading(false);
    }
  };

  const getParentCategories = async () => {
    try {
      const UPLOAD_BACKEND_URL = 'http://localhost:8080/api/v1/category/list/parent';
      const response = await axios.get(UPLOAD_BACKEND_URL, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
      });

      const fetchedCats = response.data.data;
      setCats(fetchedCats);
    } catch (error) {
      console.error('Error fetching parent categories:', error);
      setErrorMessage(error.response?.data?.message || 'Error fetching parent categories.');
    } finally {
      setIsLoading(false);
    }
  };

  const getSubCategories = async (parentCategoryName) => {
    try {
      const SUBCATEGORY_BACKEND_URL = `http://localhost:8080/api/v1/category/sub/${parentCategoryName}`;
      const response = await axios.get(SUBCATEGORY_BACKEND_URL, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
      });

      const fetchedSubCats = response.data.data;
      setSubCategories(fetchedSubCats);
    } catch (error) {
      console.error('Error fetching subcategories:', error);
      setErrorMessage(error.response?.data?.message || 'Error fetching subcategories.');
    }
  };

  const handleShowDemo = () => {
    if (image && imageName) {
      setShowCard(true);
    } else {
      setErrorMessage('Please complete the image name and upload an image to show the demo.');
    }
  };

  useEffect(() => {
    getParentCategories(); // Call the function on component mount
  }, []);

  useEffect(() => {
    if (category) {
      getSubCategories(category); // Fetch subcategories when category changes
    } else {
      setSubCategories([]); // Reset subcategories if no category is selected
    }
  }, [category]);

  const handleCloseCard = () => {
    setShowCard(false);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleCategoryAdded = () => {
    getParentCategories(); // Refresh categories after adding
  };

  return (
    <div className='UploadImage w-full h-full flex justify-center items-center bg-bgDarkBlue'>
      <div className="flex flex-col items-center justify-center bg-gray-100 p-6 rounded shadow-md w-96">
        <h2 className="text-xl font-bold mb-4 text-black">Upload Image</h2>

        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}

        {!showCard ? (
          <form onSubmit={handleUploadSubmit}>
            {/* ... Existing form fields ... */}

            <button
              type="submit"
              className={`bg-blue-500 text-white rounded p-2 w-full hover:bg-blue-600 ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? 'Uploading...' : 'Upload'}
            </button>

            {/* Add Button for Adding Category/Subcategory */}
            <button
              type="button"
              onClick={handleOpenModal}
              className="bg-yellow-500 text-white rounded p-2 mt-4 w-full hover:bg-yellow-600"
            >
              Add Category/Subcategory
            </button>
          </form>
        ) : (
          <div className="w-full">
            <Card
              image={{
                Src: URL.createObjectURL(image),
                Title: imageName
              }}
            />
            <button
              onClick={handleCloseCard}
              className="bg-red-500 text-white rounded p-2 mt-4 w-full hover:bg-red-600"
            >
              Close
            </button>
          </div>
        )}

        {!showCard && (
          <button
            onClick={handleShowDemo}
            className="bg-green-500 text-white rounded p-2 mt-4 w-full hover:bg-green-600"
          >
            Show Demo
          </button>
        )}

        {uploadedFile && (
          <div className="mt-4 text-black">
            <p>Uploaded File: {uploadedFile}</p>
          </div>
        )}
      </div>

      {/* Add the modal here */}
      <AddCategoryModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onCategoryAdded={handleCategoryAdded}
      />
    </div>
  );
}

export default UploadImage;