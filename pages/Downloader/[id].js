import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import axios from 'axios';
import Card from '@/components/templates/Card';

const Downloader = () => {
    const router = useRouter();
    const { id: fileId, title, path } = router.query;

    const handleDownload = async () => {
        if (!fileId) {
            console.warn('File ID is not available for download.');
            return; // Ensure id is available
        }

        console.log(`Initiating download for file ID: ${fileId}`);

        try {
            const response = await axios.get(`http://localhost:8080/api/v1/file/download/${fileId}`, {
                responseType: 'blob', // Important for downloading files
            });

            console.log('Download response received:', response);

            // Create a blob from the response
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = title || 'download'; // Use the title as the file name
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url); // Clean up the URL object

            console.log('Download initiated successfully.');
        } catch (error) {
            console.error('Error downloading the file:', error);
        }
    };

    return (
        <div className='downloaderPage w-full'>
            <main className='w-full h-auto flex-auto py-12'>
                <div className='flex justify-between w-full px-8'>
                    {/* w-5/6 */}
                    <div className='flex justify-between w-full h-custom-136 p-4 bg-bgDarkGray rounded'> 
                        <div className='bg-img w-1/2 h-full'>
                            <img src={`../../img/${path}`} className='w-full h-full object-contain' alt={title} />
                        </div>
                        <div className='info-img w-1/2'></div>
                    </div>
                    {/* <div className='w-1/6 p-4'>Show Ad</div> */}
                </div>

            </main>
        </div>
    );
};

export default Downloader;