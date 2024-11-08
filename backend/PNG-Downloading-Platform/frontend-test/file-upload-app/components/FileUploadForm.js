import React, {useState} from 'react';
import axios from 'axios';
import './FileUploadForm.css';

const FileUploadForm = () => {
    const [file, setFile] = useState(null);
    const [parentCategoryName, setParentCategoryName] = useState('');
    const [subCategoryNames, setSubCategoryNames] = useState(['']);
    const [dominantColors, setDominantColors] = useState(['']);
    const [style, setStyle] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [fileId, setFileId] = useState('');

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubCategoryChange = (index, value) => {
        const newSubCategories = [...subCategoryNames];
        newSubCategories[index] = value;
        setSubCategoryNames(newSubCategories);
    };

    const handleDominantColorChange = (index, value) => {
        const newColors = [...dominantColors];
        newColors[index] = value;
        setDominantColors(newColors);
    };

    const addSubCategory = () => {
        setSubCategoryNames([...subCategoryNames, '']);
    };

    const addDominantColor = () => {
        setDominantColors([...dominantColors, '']);
    };

    const handleUploadSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage(''); // Reset error message

        const formData = new FormData();
        formData.append('file', file);
        formData.append('parentCategoryName', parentCategoryName);
        subCategoryNames.forEach((name) => formData.append('subCategoryNames', name));
        dominantColors.forEach((color) => formData.append('dominantColors', color));
        formData.append('style', style);

        try {
            const UPLOAD_BACKEND_URL = 'http://localhost:8080/api/v1/file/upload';
            const response = await axios.post(UPLOAD_BACKEND_URL, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            alert(response.data.message);
        } catch (error) {
            console.error('Error uploading file:', error);
            setErrorMessage(error.response?.data?.message || 'Error uploading file.');
        }
    };

    const handleDownload = async () => {
        if (!fileId) {
            alert('Please enter a valid file ID.');
            return;
        }

        try {
            // Call the API to initiate the download
            const DOWNLOAD_BACKEND_URL = `http://localhost:8080/api/v1/file/download/${fileId}`;
            const response = await axios.get(DOWNLOAD_BACKEND_URL, {
                responseType: 'blob', // Important for downloading files
            });

            // Extract the filename from the Content-Disposition header
            // محمد حسن، این باید کار بشه. از بک اند به این شکل در هدر اسم فایل میاد:
            // Content-Disposition: attachment; filename="Screenshot 2024-05-28 110548.png" ==> این هدر. باید بتونی اکسترکت کنی، و سپس از filename اسم فایل در بیاری.
            const filename = 'downloaded_file';

            // Create a URL for the file and trigger the download
            const url = window.URL.createObjectURL(response.data);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', filename); // Set the correct file name
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error('Error downloading file:', error);
            alert('Error downloading file. Please check the file ID and try again.');
        }
    };

    return (
        <div className="form-container">
            <div className="card">
                <h2>Upload File</h2>
                {errorMessage && <div className="error-message">{errorMessage}</div>}
                <form onSubmit={handleUploadSubmit}>
                    <input type="file" onChange={handleFileChange} required/>
                    <input
                        type="text"
                        placeholder="Parent Category Name"
                        value={parentCategoryName}
                        onChange={(e) => setParentCategoryName(e.target.value)}
                        required
                    />
                    <h3>Sub Categories</h3>
                    {subCategoryNames.map((name, index) => (
                        <input
                            key={index}
                            type="text"
                            placeholder={`Sub Category ${index + 1}`}
                            value={name}
                            onChange={(e) => handleSubCategoryChange(index, e.target.value)}
                            required
                        />
                    ))}
                    <button type="button" onClick={addSubCategory}>Add Sub Category</button>

                    <h3>Dominant Colors</h3>
                    {dominantColors.map((color, index) => (
                        <input
                            key={index}
                            type="text"
                            placeholder={`Dominant Color ${index + 1}`}
                            value={color}
                            onChange={(e) => handleDominantColorChange(index, e.target.value)}
                            required
                        />
                    ))}
                    <button type="button" onClick={addDominantColor}>Add Dominant Color</button>

                    <input
                        type="text"
                        placeholder="Style"
                        value={style}
                        onChange={(e) => setStyle(e.target.value)}
                    />
                    <button type="submit">Upload</button>
                </form>
            </div>

            <div className="card">
                <h2>Download File</h2>
                <input
                    type="text"
                    placeholder="Enter File ID"
                    value={fileId}
                    onChange={(e) => setFileId(e.target.value)}
                    required
                />
                <button onClick={handleDownload}>Download</button>
            </div>
        </div>
    );
};

export default FileUploadForm;