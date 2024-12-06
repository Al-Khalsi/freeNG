import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import { SiInstructure } from "react-icons/si";
import { IoColorPaletteOutline } from "react-icons/io5";
import { FiDownload } from "react-icons/fi";
import { FaTags } from "react-icons/fa6";
import axios from 'axios';
import Link from 'next/link';

const colorHexMap = {
    'Red': '#FF0000',
    'Green': '#008000',
    'Blue': '#0000FF',
    'Yellow': '#FFFF00',
    'Orange': '#FFA500',
    'Purple': '#800080',
    'Pink': '#FFC0CB',
    'Brown': '#A52A2A',
    'Gray': '#808080',
    'Black': '#000000',
    'White': '#FFFFFF',
};

const Downloader = () => {
    const router = useRouter();
    const { id: fileId, title, path, size, width, height, lightMode, style, dominantColors, keywords } = router.query;

    // Log the keywords to the console
    useEffect(() => {
        if (keywords) {
            try {
                const keywordArray = JSON.parse(keywords); // Parse the JSON string back into an array of objects
                console.log('Keywords:', keywordArray); // Log the keywords array
            } catch (error) {
                console.error('Error parsing keywords:', error);
            }
        }
    }, [keywords]);

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

    // Function to handle keyword click
    const handleKeywordClick = async (keywordId) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/v1/keywords/fetch/${keywordId}`); // Fetch keyword details based on ID
            const keywordData = response.data.data; // Assuming your backend returns the keyword data

            console.log(`handleKeywordClick: ${keywordData}`);

            // You can now redirect to the search page with the keyword
            router.push(`/?search=${encodeURIComponent(keywordData.keyword)}`); // Redirect to search with the keyword
        } catch (error) {
            console.error('Error fetching keyword:', error);
        }
    };

    return (
        <>
            <Head>
                <title>{title}</title>

                <meta name="description"
                    content={`${title} is a free png image with
                    dimensions ${width}x${height} and size ${size} in ${style} style available for free.`} />

                <meta name="keywords" content="free images, PNG images, download images, PixelFreebies, stock photos" />

                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            </Head>
            <div className={`downloaderPage w-full`}>
                <main className='w-full h-auto flex-auto py-12'>
                    <div className='flex justify-between w-full px-8'>
                        <div className='flex justify-between w-full h-custom-136 p-4 bg-bgDarkGray rounded-lg'>
                            <div className={`bg-img w-1/2 h-full rounded p-4 ${lightMode === 'true' ? 'lightMod' : ''}`}>
                                <img src={`../../img/${path}`} className='w-full h-full object-contain' alt={title} />
                            </div>
                            <div className='info-img flex flex-col ms-8 w-1/2'>
                                <h1 className='block text-3xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>
                                    {title}
                                </h1>
                                <div className='flex mt-4 ms-1'>
                                    <div className='flex justify-between items-center bg-gray-600 px-3 py-2 rounded text-lg text-lightBlue'>
                                        <FaImage />
                                        <span className='block ml-1'>{size}</span>
                                    </div>
                                    <div className='flex justify-between items-center bg-gray-600 ml-2 px-3 py-2 rounded text-lg text-lightBlue'>
                                        <RxDimensions />
                                        <span className='block ml-1'>{`${width} × ${height}`}</span>
                                    </div>
                                </div>
                                <div className='flex items-center mt-4 ms-1 w-full'>
                                    <div className='showStyle text-lg'>
                                        <div className='flex items-center bg-gray-600 rounded px-3 py-2'>
                                            <SiInstructure />
                                            <span className='ml-2'>Style
                                                <strong> {style}</strong>
                                            </span>
                                        </div>
                                    </div>
                                    <div className='dominantColors flex items-center ml-2 py-2 px-3 text-lg bg-gray-600 rounded'>
                                        <IoColorPaletteOutline />
                                        <span className='ml-2 flex items-center'>
                                            Colors
                                            <div className='flex ml-6'>
                                                {dominantColors && dominantColors.split(',').map((color) => (
                                                    <div key={color} className='flex items-center ml-2'>
                                                        <strong className='text-white'>{color}</strong>
                                                        <span
                                                            className='w-8 h-5 rounded-full mx-2 border-black border-2'
                                                            style={{ backgroundColor: colorHexMap[color.trim()] || '#000' }} // Default to black if color not found
                                                        ></span>
                                                    </div>
                                                ))}
                                            </div>
                                        </span>
                                    </div>
                                </div>
                                <div className='showKeywords flex items-center mt-4 mx-1 py-3 px-2 text-lg bg-gray-600 rounded'>
                                    <FaTags className='ml-1' />
                                    <span className='mx-2'>Tag</span>
                                    <div className='flex flex-wrap'>
                                        {keywords && JSON.parse(keywords).map((kw) => (
                                            <button
                                                key={kw.id}
                                                onClick={() => handleKeywordClick(kw.id)}
                                                className='text-white bg-gray-700 rounded px-2 py-1 mx-1'
                                            >
                                                {kw.keyword}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                                <div className='flex justify-center w-full'>
                                    <button className="buttonDl mt-4" type="button">
                                        <span className="button__text">Download</span>
                                        <span className="button__icon">
                                            <FiDownload className='text-black text-lg' />
                                        </span>
                                    </button>
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