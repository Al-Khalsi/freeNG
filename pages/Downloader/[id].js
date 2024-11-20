import { useRouter } from 'next/router';
import { useAuth } from '@/context/AuthContext'
import { useEffect } from 'react';
import Link from 'next/link';

const Downloader = () => {
    const { token } = useAuth();
    const router = useRouter(); 
    const { id:fileId, title, path } = router.query; 

    const handleDownload = async () => {
        if (!fileId) return; // Ensure id is available

        try {
            const response = await fetch(`http://localhost:8080/api/v1/file/download/${fileId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            // Create a blob from the response
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = title || 'download'; // Use the title as the file name
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url); // Clean up the URL object
        } catch (error) {
            console.error('Error downloading the file:', error);
        }
    };

    return (
        <div className='imageSinglePage w-full min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className='wrapper p-12'>
                <div className='product-div w-full h-80 flex bg-bgDarkGray rounded-3xl m-5 overflow-hidden'>
                    <div className='product-div-left bg-img w-1/3'>
                        <div className="img-container w-full h-full">
                            <img src={`../../img/${path}`} className='w-full h-full object-cover' alt={title} />
                        </div>
                    </div>
                    <div className='product-div-right w-2/3 bg-darkBlue text-white'>
                        <h1 className='image-title'>{title}</h1>
                        <button
                            onClick={handleDownload}
                            className='mt-4 bg-blue-500 text-white py-2 px-4 rounded'
                        >
                            Download
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Downloader;