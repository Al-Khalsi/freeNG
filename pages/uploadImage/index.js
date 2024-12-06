import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '@/context/AuthContext';

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [dominantColors, setDominantColors] = useState([]); // Change to array
  const [style, setStyle] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [uploadedFile, setUploadedFile] = useState('');
  const [lightMode, setLightMode] = useState(false);
  const [selectedKeywords, setSelectedKeywords] = useState([]);
  const [isAddingKeywords, setIsAddingKeywords] = useState(false);
  const [addKeyword, setAddKeyword] = useState('');
  const [fetchedKeywords, setFetchedKeywords] = useState([]); // State to hold fetched keywords
  const [showResults, setShowResults] = useState(false); // State to control visibility of keyword results
  const [searchQuery, setSearchQuery] = useState(''); // State for search input
  const [currentPage, setCurrentPage] = useState(0); // Current page for pagination
  const [totalPages, setTotalPages] = useState(0); // Total pages for pagination
  const [showColorDropdown, setShowColorDropdown] = useState(false);

  const colors = [
    { name: 'Red', hex: '#FF0000' },
    { name: 'Green', hex: '#008000' },
    { name: 'Blue', hex: '#0000FF' },
    { name: 'Yellow', hex: '#FFFF00' },
    { name: 'Orange', hex: '#FFA500' },
    { name: 'Purple', hex: '#800080' },
    { name: 'Pink', hex: '#FFC0CB' },
    { name: 'Brown', hex: '#A52A2A' },
    { name: 'Gray', hex: '#808080' },
    { name: 'Black', hex: '#000000' },
    { name: 'White', hex: '#FFFFFF' },
  ];

  const styles = [
    '3D', 'Pixel', 'Anime', 'Cartoon', 'Realistic', 'Abstract'
  ];

  // --------------------------- Backend URLs ---------------------------
  const BACKEND_API_VERSION = "api/v1";
  const BACKEND_BASE_URL = `http://localhost:8080/${BACKEND_API_VERSION}`;
  const BACKEND_UPLOAD_URL = `${BACKEND_BASE_URL}/file`;
  const BACKEND_UPLOAD_FILE_URL = `${BACKEND_UPLOAD_URL}/upload`;
  const BACKEND_KEYWORD_SEARCH_URL = `${BACKEND_BASE_URL}/file/search/keywords`;
  const BACKEND_KEYWORD_URL = `${BACKEND_BASE_URL}/keywords`;

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
    dominantColors.forEach(color => {
      formData.append('dominantColors', color);
    });
    formData.append('style', style);
    formData.append('lightMode', lightMode);
    selectedKeywords.forEach(keyword => {
      formData.append('keywords', keyword);
    });

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
      console.log(`Error uploading image: ${error}`)
      setErrorMessage(error.response?.data?.message || 'Error uploading image.');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchKeywords = async (query, page = 0, size = 10) => {
    try {
      const response = await axios.get(`${BACKEND_KEYWORD_SEARCH_URL}/paginated`, {
        params: {
          query,
          page,
          size
        },
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
      const keywordsName = response.data.data;
      console.log(`keyword response: ${keywordsName}`);
      setFetchedKeywords(keywordsName); // Store fetched keywords
      setTotalPages(response.data.totalPages); // Set total pages from response
      setShowResults(true); // Show the results after fetching keywords
    } catch (error) {
      console.log(`Error fetching keywords: ${error}`)
      setErrorMessage(error.response?.data?.message || 'Error fetching keywords.');
    }
  };

  const handleColorChange = (color) => {
    setDominantColors(prevColors => {
      if (prevColors.includes(color)) {
        return prevColors.filter(c => c !== color); // Remove color if already selected
      } else if (prevColors.length < 3) {
        return [...prevColors, color]; // Add color if not selected and limit not reached
      }
      return prevColors; // Return current state if limit reached
    });
  };

  const handleSearch = () => {
    if (searchQuery.trim()) {
      fetchKeywords(searchQuery, currentPage); // Fetch keywords based on search query
    } else {
      setErrorMessage('Please enter a search term.');
    }
  };

  const addKeywords = async () => {
    if (!addKeyword) {
      setErrorMessage('Please enter a keyword to add.');
      return;
    }

    try {
      const response = await axios.post(BACKEND_KEYWORD_URL, {
        keyword: addKeyword
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
      console.log(`keyword response: ${response.data}`);
      setSelectedKeywords(prev => [...prev, response.data.data]); // Add the newly created keyword to selected keywords
      setAddKeyword(''); // Clear the input
      setIsAddingKeywords(false); // Hide the input field
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Error creating keyword.');
    }


  };

  const handleCheckboxChange = (keyword) => {
    setSelectedKeywords(prevSelected => {
      if (prevSelected.includes(keyword)) {
        return prevSelected.filter(k => k !== keyword); // Remove keyword if already selected
      } else {
        return [...prevSelected, keyword]; // Add keyword if not selected
      }
    });
  }
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

            <div className='mb-4 mx-2 w-1/2'>
              <label className="block mb-2">Dominant Color</label>
              <div className="relative">
                <button
                  type="button"
                  className="flex border rounded px-3 py-2 w-full bg-bgDarkGray2"
                  onClick={() => setShowColorDropdown(prev => !prev)}
                >
                  {dominantColors.length > 0 ? dominantColors.join(', ') : 'Select Colors'}
                </button>
                {showColorDropdown && (
                  <div className="absolute bg-bgDarkGray2 border rounded mt-1 w-full z-10">
                    <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
                    {colors.map((color) => (
                        <label key={color.name} className="flex justify-between items-center p-2 border-b cursor-pointer hover:bg-bgDarkGray">
                          <span className="flex items-center">
                            <span
                              className="block w-4 h-4 rounded-full mr-2"
                              style={{ backgroundColor: color.hex }} 
                            ></span>
                            {color.name}
                          </span>
                          <input
                            type="checkbox"
                            value={color.name} // Keep the value as the color name
                            checked={dominantColors.includes(color.name)} // Check if the color is selected
                            onChange={() => handleColorChange(color.name)} // Pass the color name to the handler
                            className="mr-2"
                          />
                        </label>
                      ))}
                    </div>
                  </div>
                )}
              </div>
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

          <div className='flex mb-4'>
            {isAddingKeywords ? (
              <div className='keywordsAdd flex justify-between w-full mt-2'>
                <input
                  type='text'
                  className='w-1/2 mx-2 p-2 bg-bgDarkGray2 border rounded'
                  placeholder='Add Keywords'
                  autoComplete='off'
                  value={addKeyword}
                  onChange={(e) => setAddKeyword(e.target.value)} // Update addKeyword state
                />
                <div className='w-1/2 flex justify-between mx-2'>
                  <button
                    type='button'
                    onClick={addKeywords} // Save button triggers keyword creation
                    className='w-1/2 mx-2 p-2 bg-green-700 rounded opacity-60 hover:opacity-100'>Save
                  </button>
                  <button
                    type='button'
                    onClick={handleCancelKeywords} // Cancel button
                    className='w-1/2 mx-2 p-2 bg-red-700 rounded opacity-60 hover:opacity-100'>Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className='keywordSelect flex justify-between w-full mt-2'>
                <div className='relative w-1/2 mx-2'>
                  <input
                    type='text'
                    className='p-2 w-full bg-bgDarkGray2 border rounded'
                    placeholder='Search Keywords'
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)} // Update search query
                    autoComplete='off'
                  />
                  <button
                    type='button'
                    className='absolute right-0 top-1/2 -translate-y-1/2 h-full px-2
                    text-black bg-white rounded-r'
                    onClick={handleSearch} // Fetch keywords on click
                  >Search
                  </button>
                  <div className={`result-keywordSelect absolute w-5/6 h-32
                    ${showResults ? 'flex' : 'hidden'} flex-col rounded-b bg-bgDarkGray2`}>
                    {fetchedKeywords.map((keyword, index) => (
                      <label htmlFor={`keyword-${index}`} key={index} className='flex justify-between items-center w-full p-2 border-b border-gray-400
                        cursor-pointer '>
                        <p className={'text-white'}>{keyword}</p>
                        <input type="checkbox" id={`keyword-${index}`}
                          checked={selectedKeywords.includes(keyword)} // Check if the keyword is selected
                          onChange={() => handleCheckboxChange(keyword)} // Update selected keywords on checkbox change
                        />
                      </label>
                    ))}
                  </div>
                </div>
                <button
                  type='button'
                  onClick={handleAddKeywords} // Add button
                  className='w-1/2 mx-2 p-2 bg-bgDarkGray2 rounded hover:border'>Add
                </button>
              </div>
            )}
          </div>

          <div className='flex mb-4 mx-2 px-2 bg-bgDarkGray2'>

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