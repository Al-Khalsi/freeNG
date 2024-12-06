import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import axios from 'axios';

const Downloader = () => {
    const router = useRouter();
    const { id: fileId, title, path, size, width, height, lightMode } = router.query; // Extract lightMode

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
        <>
            <Head>
                <title>PixelFreebies</title>

                <meta name="description"
                    content="PixelFreebies offers a vast collection of free PNG images for download. 
                Explore and find the perfect image for your project." />

                <meta name="keywords" content="free images, PNG images, download images, PixelFreebies, stock photos" />

                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            </Head>
            <div className={`downloaderPage w-full`}>
                <main className='w-full h-auto flex-auto py-12'>
                    <div className='flex justify-between w-full px-8'>
                        <div className='flex justify-between w-full h-custom-136 p-4 bg-bgDarkGray rounded'>
                            <div className={`bg-img w-1/2 h-full p-4 ${lightMode === 'true' ? 'lightMod' : ''}`}>
                                <img src={`../../img/${path}`} className='w-full h-full object-contain' alt={title} />
                            </div>
                            <div className='info-img flex flex-col ms-8 w-1/2'>
                                <h1 className='block text-3xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>
                                    {title}
                                </h1>
                                <div className='flex mt-4 ms-1'>
                                    <div className='flex justify-between items-center bg-gray-600 px-2 py-1 rounded text-xs text-lightBlue'>
                                        <FaImage />
                                        <span className='block ml-1'>{size}</span>
                                    </div>
                                    <div className='flex justify-between items-center bg-gray-600 ml-2 px-2 py-1 rounded text-xs text-lightBlue'>
                                        <RxDimensions />
                                        <span className='block ml-1'>{`${width} × ${height}`}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </>
    );
};

export default Downloader;