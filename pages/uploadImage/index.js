import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '@/context/AuthContext';
import { MdDelete } from "react-icons/md";

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [dominantColors, setDominantColors] = useState([]);
  const [style, setStyle] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [uploadedFile, setUploadedFile] = useState('');
  const [lightMode, setLightMode] = useState(false);
  const [selectedKeywords, setSelectedKeywords] = useState([]);
  const [isAddingKeywords, setIsAddingKeywords] = useState(false);
  const [addKeyword, setAddKeyword] = useState('');
  const [fetchedKeywords, setFetchedKeywords] = useState([]);
  const [showResults, setShowResults] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showColorDropdown, setShowColorDropdown] = useState(false); // State for color dropdown
  const dropdownRef = useRef(null);
  const inputRef = useRef(null);
  const addKeywordInputRef = useRef(null);
  const dropdownColorRef = useRef(null); // Reference for the color dropdown

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

      const uploadedFileData = response.data.data;
      setUploadedFile(uploadedFileData);
      alert('Upload successful: ' + uploadedFileData);
    } catch (error) {
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

      setFetchedKeywords(keywordsName);
      setTotalPages(response.data.totalPages);
      setShowResults(true);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Error fetching keywords.');
    }
  };

  const handleColorChange = (color) => {
    setDominantColors(prevColors => {
      if (prevColors.includes(color)) {
        return prevColors.filter(c => c !== color);
      } else if (prevColors.length < 3) {
        return [...prevColors, color];
      }
      return prevColors;
    });
  };

  const handleSearch = () => {
    if (searchQuery.trim()) {
      fetchKeywords(searchQuery, currentPage);
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
      const newKeyword = response.data.data.keyword;
      setAddKeyword('');
      setIsAddingKeywords(false);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Error creating keyword.');
    }
  };

  const handleCheckboxChange = (keyword) => {
    setSelectedKeywords(prevSelected => {
      if (prevSelected.includes(keyword)) {
        return prevSelected.filter(k => k !== keyword);
      } else {
        return [...prevSelected, keyword];
      }
    });
  };

  const toggleLightMode = () => {
    setLightMode(prevMode => !prevMode);
  };

  const handleAddKeywords = () => {
    setIsAddingKeywords(true);
    setTimeout(() => {
      if (addKeywordInputRef.current) {
        addKeywordInputRef.current.focus();
      }
    }, 0);
  };

  const handleCancelKeywords = () => {
    setIsAddingKeywords(false);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target) &&
        inputRef.current &&
        !inputRef.current.contains(event.target)
      ) {
        setShowResults(false);
      }
      if (
        dropdownColorRef.current &&
        !dropdownColorRef.current.contains(event.target)
      ) {
        setShowColorDropdown(false); // Close color dropdown
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

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
                  <div className='absolute bg-bgDarkGray2 border border-t-0 rounded w-full z-10' ref={dropdownColorRef}>
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
                            value={color.name}
                            checked={dominantColors.includes(color.name)}
                            onChange={() => handleColorChange(color.name)}
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
                  ref={addKeywordInputRef}
                  className='w-1/2 mx-2 p-2 bg-bgDarkGray2 border rounded'
                  placeholder='Add Keywords'
                  autoComplete='off'
                  value={addKeyword}
                  onChange={(e) => setAddKeyword(e.target.value)}
                />
                <div className='w-1/2 flex justify-between mx-2'>
                  <button
                    type='button'
                    onClick={addKeywords}
                    className='w-1/2 mx-2 p-2 bg-green-700 rounded opacity-60 hover:opacity-100'>Save
                  </button>
                  <button
                    type='button'
                    onClick={handleCancelKeywords}
                    className='w-1/2 mx-2 p-2 bg-red-700 rounded opacity-60 hover:opacity-100'>Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className='keywordSelect flex justify-between w-full mt-2'>
                <div className='relative w-1/2 mx-2'>
                  <input
                    type='text'
                    ref={inputRef}
                    className='p-2 w-full bg-bgDarkGray2 border rounded'
                    placeholder='Search Keywords'
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    autoComplete='off'
                  />
                  <button
                    type='button'
                    className='absolute right-0 top-1/2 -translate-y-1/2 h-full px-2
                    text-black bg-white rounded-r'
                    onClick={handleSearch}
                  >Search
                  </button>
                  <div className={`result-keywordSelect absolute w-5/6 max-h-32
                    ${showResults ? 'flex' : 'hidden'} flex-col rounded-b bg-bgDarkGray2 overflow-y-auto`}
                    ref={dropdownRef}>
                    {fetchedKeywords.map((keyword, index) => (
                      <label htmlFor={`keyword-${index}`} key={index} className='flex justify-between items-center w-full p-2 border-b border-gray-400
                        cursor-pointer '>
                        <p className={'text-white'}>{keyword}</p>
                        <input type="checkbox" id={`keyword-${index}`}
                          checked={selectedKeywords.includes(keyword)}
                          onChange={() => handleCheckboxChange(keyword)}
                        />
                      </label>
                    ))}
                  </div>
                </div>
                <button
                  type='button'
                  onClick={handleAddKeywords}
                  className='w-1/2 mx-2 p-2 bg-bgDarkGray2 rounded hover:border'>Add
                </button>
              </div>
            )}
          </div>

          {selectedKeywords.length > 0 && (
            <div className='flex items-center flex-wrap mt-4 mx-2 pt-2 px-2 rounded bg-bgDarkGray2'>
              {selectedKeywords.map((keyword, Index) => (
                <div key={`${keyword}-${Index}`} className="flex items-center mx-1 mb-2 p-1 rounded-sm bg-gray-600">
                  <p className="mr-1">{keyword}</p>
                  <button
                    type="button"
                    onClick={() => handleCheckboxChange(keyword)}
                    className="flex items-center justify-center w-6 h-6 text-white hover:text-red-600"
                    title={`Remove ${keyword}`}>
                    <MdDelete />
                  </button>
                </div>
              ))}
            </div>
          )}

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
      </div>
    </div>
  );
}

export default UploadImage;