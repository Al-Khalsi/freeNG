import MainLayout from '@/layouts/MainLayout';
import React, { useState } from 'react';

function Convert({ supportedFormats }) {
  const [file, setFile] = useState(null);
  const [outputFormat, setOutputFormat] = useState('jpg');
  const [loading, setLoading] = useState(false);
  const [convertedImage, setConvertedImage] = useState(null);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  // Handle file selection
  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
    setError('');
    setConvertedImage(null);
  };

  // Handle format selection
  const handleFormatChange = (event) => {
    setOutputFormat(event.target.value);
  };

  // Handle image conversion
  const handleConvert = async () => {
    if (!file) {
      setError('Please select a file.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const reader = new FileReader();
      reader.onloadend = async () => {
        const img = new Image();
        img.src = reader.result;
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
          setConvertedImage(dataUrl);
          setLoading(false);
        };
      };
      reader.readAsDataURL(file);
    } catch (err) {
      setError('Error converting image.');
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
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
      handleSearch={handleSearch}>
      <h1>Image Format Converter</h1>
      <input type="file" accept="image/*" onChange={handleFileChange} />
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