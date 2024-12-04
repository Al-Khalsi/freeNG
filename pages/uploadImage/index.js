import React, { useState } from 'react';
import axios from 'axios';
import Card from '@/components/templates/Card';
import { useAuth } from '@/context/AuthContext';

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [dominantColor, setDominantColor] = useState('');
  const [style, setStyle] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [uploadedFile, setUploadedFile] = useState('');
  const [lightMode, setLightMode] = useState(false);
  const [selectedKeywords, setSelectedKeywords] = useState([]);
  const [isAddingKeywords, setIsAddingKeywords] = useState(false); // New state for toggling keyword sections

  const colors = [
    'Red', 'Green', 'Blue', 'Yellow', 'Orange', 'Purple',
    'Pink', 'Brown', 'Gray', 'Black', 'White',
  ];

  const styles = [
    '3D', 'Pixel', 'Anime', 'Cartoon', 'Realistic', 'Abstract'
  ];

  const keywords = [
    'Nature', 'Technology', 'Art', 'Science', 'Travel', 'Food', 'Fashion', 'Sports'
  ];

  // --------------------------- Backend URLs ---------------------------
  const BACKEND_API_VERSION = "api/v1";
  const BACKEND_BASE_URL = `http://localhost:8080/${BACKEND_API_VERSION}`;
  const BACKEND_UPLOAD_URL = `${BACKEND_BASE_URL}/file`;
  const BACKEND_UPLOAD_FILE_URL = `${BACKEND_UPLOAD_URL}/upload`;

  const handleImageChange = (e) => {
    const image = e.target.files[0];
    if (image && image.type.startsWith('image/')) {
      setImage(image);
      setErrorMessage('');
    } else {
      setErrorMessage('Please select a valid image.');
    }
  };

  const handleUploadSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    if (!image) {
      setErrorMessage('Please upload an image.');
      return;
    }

    setIsLoading(true);
    const formData = new FormData();
    formData.append('file', image);
    formData.append('fileName', imageName);
    formData.append('dominantColors', dominantColor);
    formData.append('style', style);
    formData.append('lightMode', lightMode);
    formData.append('keywords', JSON.stringify(selectedKeywords)); // Send selected keywords as a JSON string

    try {
      const response = await axios.post(BACKEND_UPLOAD_FILE_URL, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`
        },
      });

      const uploadedFileData = response.data.image;
      setUploadedFile(uploadedFileData);
      alert('Upload successful: ' + uploadedFileData);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Error uploading image.');
    } finally {
      setIsLoading(false);
    }
  };

  const toggleLightMode = () => {
    setLightMode(prevMode => !prevMode);
  };

  const handleAddKeywords = () => {
    setIsAddingKeywords(true); // Show keywordsAdd section
  };

  const handleCancelKeywords = () => {
    setIsAddingKeywords(false); // Hide keywordsAdd section
  };

  return (
    <div className={`UploadImage w-full min-h-dvh py-12 flex justify-center items-center bg-bgDarkBlue`}>
      <div className="w-custom-212 p-6 bg-bgDarkGray text-clWhite rounded shadow-md">
        <h2 className="text-xl font-bold mb-4">Upload Image</h2>

        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}

        <form onSubmit={handleUploadSubmit} className='w-full'>

          <div className='flex items-center'>
            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Image</label>
              <div
                className="border-dashed border-2 border-gray-400 
                rounded p-2 w-full bg-bgDarkGray2 cursor-pointer 
                flex items-center justify-center hover:bg-bgDarkGray"
                onClick={() => document.getElementById('file-input').click()}
              >
                {image ? (
                  <p>{image.name}</p>
                ) : (
                  <p>Click to select image</p>
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                  id="file-input"
                />
              </div>
            </div>

            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Image Name</label>
              <input
                type="text"
                value={imageName}
                onChange={(e) => setImageName(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
                autoComplete="off"
              />
            </div>
          </div>

          <div className='flex items-center'>
            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Dominant Color</label>
              <select
                value={dominantColor}
                onChange={(e) => setDominantColor(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              >
                <option value="">Select a color</option>
                {colors.map((color) => (
                  <option key={color} value={color}>
                    {color}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Style</label>
              <select
                value={style}
                onChange={(e) => setStyle(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              >
                <option value="">Select a style</option>
                {styles.map((styleOption) => (
                  <option key={styleOption} value={styleOption}>
                    {styleOption}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Keyword Selection Section */}
          <div className='flex mb-4'>
            {isAddingKeywords ? (
              <div className='keywordsAdd flex justify-between w-full mt-2'>
                <input type='text' className='w-1/2 mx-2 p-2 bg-bgDarkGray2 border rounded'
                  placeholder='Add Keywords' autoComplete='off' />
                <div className='w-1/2 flex justify-between mx-2'>
                  <button
                    type='button'
                    className='w-1/2 mx-2 p-2 bg-green-700 rounded opacity-60 hover:opacity-100'>Save</button>
                  <button
                    type='button'
                    onClick={handleCancelKeywords} // Cancel button
                    className='w-1/2 mx-2 p-2 bg-red-700 rounded opacity-60 hover:opacity-100'>Cancel</button>
                </div>
              </div>
            ) : (
              <div className='keywordSelect flex justify-between w-full mt-2'>
                <div className='relative w-1/2 mx-2'>
                  <input type='text'
                    className='p-2 w-full bg-bgDarkGray2 border rounded'
                    placeholder='Select Keywords' autoComplete='off' />
                  <button type='button'
                    className='absolute right-0 top-1/2 -translate-y-1/2 h-full px-2 
                    text-black bg-white rounded-r'>Search</button>
                </div>
                <button type='button'
                  onClick={handleAddKeywords} // Add button
                  className='w-1/2 mx-2 p-2 bg-bgDarkGray2 rounded hover:border'>Add</button>
              </div>
            )}
          </div>

          <div className='flex justify-center items-center mt-4'>
            <button
              type="submit"
              className={`bg-bgDarkBlue text-white rounded mx-2 p-2 w-full hover:border
              ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
              disabled={isLoading}>
              {isLoading ? 'Uploading...' : 'Upload'}
            </button>
            <button
              type="button"
              onClick={toggleLightMode}
              className={`rounded p-2 mx-2 w-full hover:border 
              ${lightMode ? 'bg-white text-black' : 'bg-black text-white'}`}>
              {lightMode ? 'Disable Light Mode' : 'Enable Light Mode'}
            </button>
          </div>

        </form>

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