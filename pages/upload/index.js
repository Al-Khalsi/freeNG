import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '@/context/AuthContext';
import { MdDelete } from "react-icons/md";
import { KEYWORD_API } from "@/utils/api/keyword";
import { FILE_API } from "@/utils/api/file";
import { LuUpload } from "react-icons/lu";
import { FaSun, FaMoon } from "react-icons/fa";
import Button from '@/components/modules/Button';
import Input from '@/components/modules/Input';
import Selector from '@/components/templates/Selector';

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState(''); // State for image preview
  const [imageName, setImageName] = useState('');
  const [dominantColors, setDominantColors] = useState([]);
  const [style, setStyle] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [uploadedFile, setUploadedFile] = useState('');
  const [lightMode, setLightMode] = useState(false);
  const [lightModePreview, setLightModePreview] = useState(false)
  const [selectedKeywords, setSelectedKeywords] = useState([]);
  const [isAddingKeywords, setIsAddingKeywords] = useState(false);
  const [addKeyword, setAddKeyword] = useState('');
  const [fetchedKeywords, setFetchedKeywords] = useState([]);
  const [showResults, setShowResults] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [source, setSource] = useState('');
  const [showColorDropdown, setShowColorDropdown] = useState(false);
  const dropdownRef = useRef(null);
  const inputRef = useRef(null);
  const addKeywordInputRef = useRef(null);
  const dropdownColorRef = useRef(null);
  const [selectedStyles, setSelectedStyles] = useState([]);
  const [showStyleDropdown, setShowStyleDropdown] = useState(false);
  const colorDropdownRef = useRef(null);
  const styleDropdownRef = useRef(null);

  const colors = [
    { name: 'Black', hex: '#000000' },
    { name: 'Gray', hex: '#555555' },
    { name: 'White', hex: '#FFFFFF' },
    { name: 'Light Red', hex: '#fb4545' },
    { name: 'Red', hex: '#FF0000' },
    { name: 'Dark Red', hex: '#6f0000' },
    { name: 'Light Green', hex: '#46ff46' },
    { name: 'Green', hex: '#008000' },
    { name: 'Dark Green', hex: '#003300' },
    { name: 'Light Blue', hex: '#40a0ff' },
    { name: 'Blue', hex: '#0000FF' },
    { name: 'Dark Blue', hex: '#00003e' },
    { name: 'Light Yellow', hex: '#fdfd5d' },
    { name: 'Yellow', hex: '#FFFF00' },
    { name: 'Dark Yellow', hex: '#7a6800' },
    { name: 'Light Orange', hex: '#fbaa30' },
    { name: 'Orange', hex: '#FFA500' },
    { name: 'Dark Orange', hex: '#854900' },
    { name: 'Light Purple', hex: '#ae4bff' },
    { name: 'Purple', hex: '#800080' },
    { name: 'Dark Purple', hex: '#4B0082' },
    { name: 'Light Pink', hex: '#ff7ad1' },
    { name: 'Pink', hex: '#ff00c8' },
    { name: 'Dark Pink', hex: '#540043' },
    { name: 'Light Brown', hex: '#ffa654' },
    { name: 'Brown', hex: '#992202' },
    { name: 'Dark Brown', hex: '#410e00' },
  ];

  const styles = [
    '3D',
    'Anime',
    'Cartoon',
    'Fantasy',
    'Flat',
    'Logo',
    'Isometric',
    'Outline',
    'Pixel',
    'Realistic',
  ];

  const handleImageChange = (e) => {
    const imageFile = e.target.files[0];
    if (imageFile && imageFile.type.startsWith('image/')) {
      setImage(imageFile);
      setImagePreviewUrl(URL.createObjectURL(imageFile)); // Create a preview URL
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
    formData.append('style', style);
    formData.append('lightMode', lightMode);
    formData.append('source', source);
    dominantColors.forEach(color => {
      formData.append('dominantColors', color);
    });
    selectedKeywords.forEach(keyword => {
      formData.append('keywords', keyword);
    });

    try {
      const response = await axios.post(FILE_API.UPLOAD, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`
        },
      });

      const uploadedFileData = response.data.data;
      setUploadedFile(uploadedFileData);
      alert('Upload successful');
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Error uploading image.');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchKeywords = async (query, page = 0, size = 10) => {
    try {
      const url = KEYWORD_API.SEARCH_PAGINATED(query, page, size);
      const response = await axios.get(url, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
      setFetchedKeywords(response.data.data);
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
    const trimmedQuery = searchQuery.trim();
    if (trimmedQuery) {
      fetchKeywords(trimmedQuery, currentPage);
      setShowResults(true); // Show results dropdown after fetching
    } else {
      setErrorMessage('Please enter a search term.');
      setShowResults(false); // Hide results if no query
    }
  };

  const addKeywords = async () => {
    if (!addKeyword) {
      setErrorMessage('Please enter a keyword to add.');
      return;
    }

    try {
      const response = await axios.post(KEYWORD_API.KEYWORD_URL, {
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
    setLightModePreview(prevMode => !prevMode);
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
        !inputRef.current.contains(event.target) &&
        colorDropdownRef.current &&
        !colorDropdownRef.current.contains(event.target) &&
        styleDropdownRef.current &&
        !styleDropdownRef.current.contains(event.target)
      ) {
        setShowResults(false);
        setShowStyleDropdown(false);
        setShowColorDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleStyleChange = (styleOption) => {
    setSelectedStyles(prevStyles => {
      if (prevStyles.includes(styleOption)) {
        return prevStyles.filter(style => style !== styleOption); // Remove style if already selected
      } else {
        return [...prevStyles, styleOption]; // Add style if not selected
      }
    });
  };

  return (
    <div className={`UploadImage w-full min-h-dvh flex justify-center items-center bg-bgDarkBlue`}>
      <div className="w-custom-212 p-6 bg-bgDarkGray text-clWhite rounded shadow-md">
        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}

        <form onSubmit={handleUploadSubmit} className='w-full'>

          <div className='flex items-center flex-col'>
            <div className="mx-2 w-full md-w-1/2">
              <div
                className={`border-dashed border-2 rounded p-2 h-48 sm:h-80 w-full 
                ${lightModePreview ? 'bg-bgGray text-clDarkGray2 border-bgDarkGray2' :
                    'bg-bgDarkGray2 text-clGray border-bgGray'} 
                     ${image ? 'border-green-500' : ''}
                cursor-pointer flex items-center justify-center`}
                onClick={() => document.getElementById('file-input').click()}>
                {image ? (
                  <>
                    <img
                      src={imagePreviewUrl}
                      alt="Preview"
                      className="w-full h-full rounded object-contain"
                    />
                  </>
                ) : (
                  <p>Click to select image</p>
                )}
                <Input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                  id="file-input"
                />
              </div>
            </div>

            <div className="mt-4 mx-2 w-full sm:w-1/2">
              <Input
                type="text"
                placeholder='Image Name'
                value={imageName}
                onChange={(e) => setImageName(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
                autoComplete="off"
              />
            </div>
          </div>

          <div className='flex items-center flex-col sm:flex-row mt-4'>
              <Selector
                options={colors}
                selectedOptions={dominantColors}
                onChange={handleColorChange}
                title="Colors"
                ref={colorDropdownRef}
              />

              <Selector
                options={styles}
                selectedOptions={selectedStyles}
                onChange={handleStyleChange}
                title="Styles"
                ref={styleDropdownRef} 
              />
          </div>

          <div className='flex mt-4'>
            {isAddingKeywords ? (
              <div className='keywordsAdd flex flex-col sm:flex-row justify-between w-full'>
                <Input
                  type='text'
                  ref={addKeywordInputRef}
                  placeholder='Add Keywords'
                  autoComplete='off'
                  value={addKeyword}
                  onChange={(e) => setAddKeyword(e.target.value)}
                  className='w-full sm:w-1/2 mx-0 sm:mx-2 p-2 bg-bgDarkGray2 border rounded'
                />
                <div className='w-full sm:w-1/2 flex justify-between mx-0 sm:mx-2 mt-4 sm:mt-0'>
                  <Button
                    type='button'
                    onClick={addKeywords}
                    className='w-full sm:w-1/2 mr-2 sm:ml-2 p-2 bg-green-700 rounded opacity-100 hover:opacity-80'
                  >
                    Save
                  </Button>
                  <Button
                    type='button'
                    onClick={handleCancelKeywords}
                    className='w-full sm:w-1/2 ml-2 sm:mr-2 p-2 bg-red-700 rounded opacity-100 hover:opacity-80'
                  >
                    Cancel
                  </Button>
                </div>
              </div>
            ) : (
              <div className='keywordSelect flex flex-col sm:flex-row justify-between w-full'>
                <div className='relative w-full sm:w-1/2 mx-0 sm:mx-2'>
                  <Input
                    type='text'
                    ref={inputRef}
                    placeholder='Search Keywords'
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className='p-2 w-full bg-bgDarkGray2 border focus:outline-none rounded'
                  />
                  <Button
                    type='button'
                    className={`absolute right-0 top-1/2 -translate-y-1/2 h-full px-2 rounded-r
                      ${showResults ? 'bg-red-700' : 'bg-green-700'} text-white`}
                    onClick={() => {
                      if (showResults) {
                        setShowResults(false);
                      } else {
                        handleSearch();
                      }
                    }}
                  >
                    {showResults ? 'Close' : 'Search'}
                  </Button>
                  <div className={`result-keywordSelect absolute w-5/6 max-h-32
                    ${showResults ? 'flex' : 'hidden'} flex-col rounded-b bg-bgDarkGray2 overflow-y-auto z-50`}
                    ref={dropdownRef}>
                    {fetchedKeywords.map((keyword, index) => (
                      <label htmlFor={`keyword-${index}`} key={index}
                        className='flex justify-between items-center w-full p-2
                        border-b border-gray-400 cursor-pointer '>
                        <p className={'text-white'}>{keyword}</p>
                        <Input
                          type="checkbox"
                          id={`keyword-${index}`}
                          checked={selectedKeywords.includes(keyword)}
                          onChange={() => handleCheckboxChange(keyword)}
                        />
                      </label>
                    ))}
                  </div>
                </div>
                <Button
                  type='button'
                  onClick={handleAddKeywords}
                  className='w-full sm:w-1/2 mx-0 sm:mx-2 mt-4 sm:mt-0 p-2 bg-bgDarkGray2 rounded hover:border'
                >
                  Add
                </Button>
              </div>
            )}
          </div>

          {selectedKeywords.length > 0 && (
            <div className='flex items-center flex-wrap mt-4 mx-2 pt-2 px-2 rounded bg-bgDarkGray2'>
              {selectedKeywords.map((keyword, Index) => (
                <div key={`${keyword}-${Index}`} className="flex items-center mx-1 mb-2 p-1 rounded-sm bg-gray-600">
                  <p className="mr-1">{keyword}</p>
                  <Button
                    type="button"
                    onClick={() => handleCheckboxChange(keyword)}
                    className="flex items-center justify-center w-6 h-6 text-white hover:text-red-600"
                    title={`Remove ${keyword}`}
                  >
                    <MdDelete />
                  </Button>
                </div>
              ))}
            </div>
          )}

          <div className='hidden sm:flex flex-col sm:flex-row justify-center items-center mt-4'>
            <Button
              type="submit"
              className={`bg-bgDarkBlue text-white rounded w-full sm:w-1/2 mx-2 p-2 hover:border
              ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? 'Uploading...' : 'Upload'}
            </Button>
            <div className='flex flex-col sm:flex-row justify-between items-center w-full mx-2 sm:w-1/2'>
              <Button
                type="button"
                onClick={toggleLightMode}
                className={`rounded p-2 mr-2 w-full hover:border 
              ${lightMode ? 'bg-white text-black' : 'bg-black text-white'}`}
              >
                {lightMode ? 'Disable Light Mode' : 'Enable Light Mode'}
              </Button>
              <Input
                type="text"
                className='INPUTSource rounded ml-2 mt-4 sm:mt-0 p-2 w-full bg-bgDarkGray2 opacity-50'
                value={source}
                onChange={(e) => setSource(e.target.value)}
                placeholder='PixelFreebies'
              />
            </div>
          </div>

          {/* mobile ui */}
          <div className='flex sm:hidden flex-col sm:flex-row justify-center items-center mt-4'>
            <Input
              type="text"
              className='INPUTSource rounded mx-2 sm:mt-0 p-2 w-full bg-bgDarkGray2 opacity-50'
              value={source}
              onChange={(e) => setSource(e.target.value)}
              placeholder='PixelFreebies'
            />
            <div className='flex flex-row justify-between items-center w-full sm:w-1/2 mt-4'>
              <Button
                type="submit"
                className={`bg-bgDarkBlue flex justify-center text-xl text-white rounded w-full sm:w-1/2 mr-2 sm:ml-2 p-2 hover:border
              ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
                disabled={isLoading}>
                {isLoading ? '...' : <LuUpload />}
              </Button>
              <Button
                type="button"
                onClick={toggleLightMode}
                className={`flex justify-center p-2 ml-2 sm:mr-2 w-full text-xl font-bold rounded
              ${lightMode ? 'bg-white text-black border-none' : 'bg-black text-white border-none'}`}>
                {lightMode ? <FaSun /> : <FaMoon />}
              </Button>
            </div>
          </div>

        </form>
      </div>
    </div>
  );
}

export default UploadImage;