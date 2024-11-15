import React, { useState } from 'react';
import axios from 'axios'; // Import axios
import Card from '@/components/templates/Card'; // فرض بر این است که کامپوننت Card در همان دایرکتوری است.
import { useAuth } from '../../context/AuthContext'; // Adjust the path as necessary

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

  const handleShowDemo = () => {
    if (image && imageName) {
      setShowCard(true);
    } else {
      setErrorMessage('Please complete the image name and upload an image to show the demo.');
    }
  };

  const handleCloseCard = () => {
    setShowCard(false);
  };

  return (
    <div className='UploadImage w-full h-full flex justify-center items-center bg-bgDarkBlue'>
      <div className="flex flex-col items-center justify-center bg-gray-100 p-6 rounded shadow-md w-96">
        <h2 className="text-xl font-bold mb-4 text-black">Upload Image</h2>

        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}

        {!showCard ? (
          <form onSubmit={handleUploadSubmit}>
            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Image</label>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="border rounded p-2 w-full text-black"
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Image Name</label>
              <input
                type="text"
                value={imageName}
                onChange={(e) => setImageName(e.target.value)}
                className="border rounded p-2 w-full text-black"
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Category</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="border rounded p-2 w-full text-black"
                required
              >
                <option value="">Select a category</option>
                <option value="category1">Category 1</option>
                <option value="category2">Category 2</option>
                <option value="category3">Category 3</option>
              </select>
            </div>

            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Subcategory</label>
              <select
                value={subCategory}
                onChange={(e) => setSubCategory(e.target.value)}
                className="border rounded p-2 w-full text-black"
                required
              >
                <option value="">Select a subcategory</option>
                <option value="subcategory1">Subcategory 1</option>
                <option value="subcategory2">Subcategory 2</option>
                <option value="subcategory3">Subcategory 3</option>
              </select>
            </div>

            <button
              type="submit"
              className={`bg-blue-500 text-white rounded p-2 w-full hover:bg-blue-600 ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? 'Uploading...' : 'Upload'}
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

        {/* Optionally display the uploaded file info */}
        {uploadedFile && (
          <div className="mt-4 text-black">
            <p>Uploaded File: {uploadedFile}</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default UploadImage;