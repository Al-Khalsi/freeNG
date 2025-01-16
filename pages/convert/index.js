import Input from '@/components/modules/Input';
import MainLayout from '@/layouts/MainLayout';
import React, { useState } from 'react';

function Convert({ supportedFormats }) {
  const [file, setFile] = useState(null);
  const [outputFormat, setOutputFormat] = useState('jpg');
  const [loading, setLoading] = useState(false);
  const [convertedImage, setConvertedImage] = useState(null);
  const [error, setError] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [lightModePreview, setLightModePreview] = useState(false);
  const [image, setImage] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState('');

  const handleImageChange = (e) => {
    setFile(e.target.files[0]);
    setError('');
    setConvertedImage(null);
    const imageFile = e.target.files[0];
    if (imageFile && imageFile.type.startsWith('image/')) {
      setImage(imageFile);
      setImagePreviewUrl(URL.createObjectURL(imageFile)); // Create a preview URL
      setErrorMessage('');
    } else {
      setErrorMessage('Please select a valid image.');
    }
  };

  const toggleLightMode = () => {
    setLightMode(prevMode => !prevMode);
    setLightModePreview(prevMode => !prevMode);
  };

  // Handle format selection
  const handleFormatChange = (event) => {
    setOutputFormat(event.target.value);
  };

  // Handle image conversion
  const handleConvert = async () => {
    if (!file) {
      setError('Please select a file.'); // Error message for no file selected
      return;
    }

    setLoading(true);
    setError('');

    try {
      const reader = new FileReader();
      reader.onloadend = async () => {
        const arrayBuffer = reader.result; // Get the ArrayBuffer
        const blob = new Blob([arrayBuffer], { type: file.type }); // Create a Blob from the ArrayBuffer
        const img = new Image();
        const imgUrl = URL.createObjectURL(blob); // Create a URL for the Blob

        img.src = imgUrl;
        img.onload = async () => {
          const canvas = document.createElement('canvas');
          const ctx = canvas.getContext('2d');
          canvas.width = img.width;
          canvas.height = img.height;

          // Draw the image on the canvas
          ctx.drawImage(img, 0, 0);

          // If the output format is JPG, fill the background with white
          if (outputFormat === 'jpg') {
            ctx.fillStyle = 'white';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.drawImage(img, 0, 0);
          }

          // Convert the canvas to the desired format
          const dataUrl = canvas.toDataURL(`image/${outputFormat}`);
          setConvertedImage(dataUrl); // Set the converted image
          setLoading(false);
        };
      };

      reader.readAsArrayBuffer(file); // Read the file as an ArrayBuffer
    } catch (err) {
      setError('Error converting image.'); // Error message for conversion failure
      setLoading(false);
    }
  };

  // Handle image download
  const handleDownload = () => {
    if (convertedImage) {
      const link = document.createElement('a');
      link.href = convertedImage;
      link.download = `converted_image.${outputFormat}`;
      link.click();
    }
  };

  const handleSearch = () => {
    const trimmedSearchQuery = searchQuery.trim();
    if (!trimmedSearchQuery) {
      return; // Do not proceed if the search query is empty
    }
    setSubmittedSearchQuery(trimmedSearchQuery);
    router.push(`/search?query=${encodeURIComponent(trimmedSearchQuery)}`);
  };

  return (
    <MainLayout
      pageTitle="Image Format Converter"
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
      handleSearch={handleSearch}
      className="app w-full flex flex-col relative"
      mainTagClassName="main w-full py-4 px-80"
    >
      <div
        className={`border-dashed border-4 rounded p-2 h-48 sm:h-80 w-full
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
      <select value={outputFormat} onChange={handleFormatChange}>
        {supportedFormats.map((format) => (
          <option key={format} value={format}>
            {format.toUpperCase()}
          </option>
        ))}
      </select>
      <button onClick={handleConvert} disabled={loading}>
        {loading ? 'Converting...' : 'Convert'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {convertedImage && (
        <div>
          <img src={convertedImage} alt="Converted" />
          <button onClick={handleDownload}>Download Converted Image</button>
        </div>
      )}
    </MainLayout>
  );
}

// Fetch supported formats using getStaticProps
export async function getStaticProps() {
  // Assume supported formats are loaded from an API or another source
  const supportedFormats = ['jpg', 'png', 'gif'];

  return {
    props: {
      supportedFormats,
    },
    revalidate: 10, // The page will be revalidated every 10 seconds
  };
}

export default Convert;