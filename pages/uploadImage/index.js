import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Card from '@/components/templates/Card';
import { useAuth } from '@/context/AuthContext';
import AddCategoryModal from '@/components/templates/AddCategoryModal';

function UploadImage() {
  const { token } = useAuth();
  const [image, setImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [category, setCategory] = useState('');
  const [subCategory, setSubCategory] = useState('');
  const [dominantColor, setDominantColor] = useState('');
  const [style, setStyle] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [showCard, setShowCard] = useState(false);
  const [uploadedFile, setUploadedFile] = useState('');
  const [cats, setCats] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [lightMode, setLightMode] = useState(false);

  // Static options for colors and styles
  const colors = [
    'Red', 'Green', 'Blue', 'Yellow', 'Orange', 'Purple',
    'Pink', 'Brown', 'Gray', 'Black', 'White', 'Cyan'
  ];

  const styles = [
    '3D', 'Pixel', 'Anime', 'Cartoon', 'Realistic', 'Abstract'
  ];

  // --------------------------- Backend URLs ---------------------------
  const BACKEND_API_VERSION = "api/v1";
  const BACKEND_BASE_URL = `http://localhost:8080/${BACKEND_API_VERSION}`;
  const BACKEND_UPLOAD_URL = `${BACKEND_BASE_URL}/file`;
  const BACKEND_CATEGORY_URL = `${BACKEND_BASE_URL}/category`;

  const BACKEND_UPLOAD_FILE_URL = `${BACKEND_UPLOAD_URL}/upload`;
  const BACKEND_LIST_PARENT_CATEGORIES_URL = `${BACKEND_CATEGORY_URL}/list/parent`;

  const handleImageChange = (e) => {
    const image = e.target.files[0];
    if (image && image.type.startsWith('image/')) {
      setImage(image);
      setErrorMessage('');
    } else {
      setErrorMessage('Please select a valid image image.');
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
    formData.append('parentCategoryName', category);
    formData.append('subCategoryNames', subCategory);
    formData.append('dominantColors', dominantColor); // Use the selected color
    formData.append('style', style); // Use the selected style
    formData.append('lightMode', lightMode);

    console.log('Uploading with formData:', {
      image: image,
      fileName: imageName,
      parentCategoryName: category,
      subCategoryNames: subCategory,
      dominantColors: dominantColor,
      style: style,
      lightMode
    });

    try {
      const response = await axios.post(BACKEND_UPLOAD_FILE_URL, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`
        },
      });

      console.log('Upload response:', response.data);
      const uploadedFileData = response.data.image; // Assuming response body is { "image": "string" }
      setUploadedFile(uploadedFileData);
      alert('Upload successful: ' + uploadedFileData);
    } catch (error) {
      console.error('Error uploading image:', error);
      setErrorMessage(error.response?.data?.message || 'Error uploading image.');
    } finally {
      setIsLoading(false);
    }
  };

  const getParentCategories = async () => {
    try {
      const response = await axios.get(BACKEND_LIST_PARENT_CATEGORIES_URL, {
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
      const response = await axios.get(`${BACKEND_CATEGORY_URL}/sub/${parentCategoryName}`, {
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
    getParentCategories();
  }, []);

  useEffect(() => {
    if (category) {
      getSubCategories(category);
    } else {
      setSubCategories([]);
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
    getParentCategories();
  };

  const toggleLightMode = () => {
    setLightMode(prevMode => !prevMode);
  };

  return (
    <div className={`UploadImage w-full min-h-dvh py-12 flex justify-center items-center bg-bgDarkBlue`}>
      <div className="w-custom-212 p-6 bg-bgDarkGray text-clWhite rounded shadow-md">
        <h2 className="text-xl font-bold mb-4">Upload Image</h2>

        {errorMessage && <p className="text-red-500 mb-4">{errorMessage}</p>}

        {/* {!showCard ? ( */}
        <form onSubmit={handleUploadSubmit} className='w-full'>

          <div className='flex items-center'>
            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Image</label>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              />
            </div>

            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Image Name</label>
              <input
                type="text"
                value={imageName}
                onChange={(e) => setImageName(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              />
            </div>
          </div>

          <div className='flex items-center'>
            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Category</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              >
                {cats.length === 0 ? (
                  <option value="">No categories available</option> // Show when no categories are available
                ) : (
                  <>
                    <option value="">Select a category</option> // Default option when categories are available
                    {cats.map((cat) => (
                      <option key={cat.name} value={cat.name}>
                        {cat.name}
                      </option>
                    ))}
                  </>
                )}
              </select>
            </div>

            <div className="mb-4 mx-2 w-1/2">
              <label className="block mb-2">Subcategory</label>
              <select
                value={subCategory}
                onChange={(e) => setSubCategory(e.target.value)}
                className="border rounded p-2 w-full bg-bgDarkGray2"
                required
              >
                {subCategories.length === 0 ? (
                  <option value="">No subcategories available</option> // Show when no subcategories are available
                ) : (
                  <>
                    <option value="">Select a subcategory</option> // Default option when subcategories are available
                    {subCategories.map((subCat) => (
                      <option key={subCat.name} value={subCat.name}>
                        {subCat.name}
                      </option>
                    ))}
                  </>
                )}
              </select>
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

          {!showCard && (
            <div className='flex justify-center items-center'>

              <button
                type="button"
                onClick={toggleLightMode}
                className={`rounded p-2 mt-4 mx-2 w-full ${lightMode ? 'bg-white text-black' : 'bg-black text-white'}`}>
                {lightMode ? 'Disable Light Mode' : 'Enable Light Mode'}
              </button>


              <button
                // onClick={handleShowDemo}
                className="bg-bgDarkGray2 text-white rounded p-2 mt-4 mx-2 w-full hover:border"
              >
                Show Demo
              </button>
            </div>
          )}

          <div className='flex justify-center items-center mt-3'>
            <button
              type="submit"
              className={`bg-bgDarkBlue text-white rounded mx-2 p-2 w-full hover:border ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? 'Uploading...' : 'Upload'}
            </button>

            <button
              type="button"
              onClick={handleOpenModal}
              className="bg-bgDarkGray2 text-white rounded mx-2 p-2 w-full hover:border"
            >
              Add
            </button>
          </div>
        </form>
        {/* ) : ( */}
        {/* <div className="w-full">
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
        </div> */}
        {/* )} */}



        {uploadedFile && (
          <div className="mt-4 text-black">
            <p>Uploaded File: {uploadedFile}</p>
          </div>
        )}
      </div>

      <AddCategoryModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onCategoryAdded={handleCategoryAdded}
      />
    </div>
  );
}

export default UploadImage;