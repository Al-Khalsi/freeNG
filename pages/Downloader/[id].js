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
import KeywordsModal from '@/components/templates/KeywordsModal';

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

    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [parsedKeywords, setParsedKeywords] = useState([]); // State to hold parsed keywords

    useEffect(() => {
        if (keywords) {
            try {
                const keywordArray = JSON.parse(keywords);
                console.log('Keywords:', keywordArray);
                setParsedKeywords(keywordArray); // Store parsed keywords in state
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
            const response = await axios.get(`http://localhost:8080/api/v1/file/keyword/${keywordId}?page=0&size=50`); // Fetch keyword details based on ID
            const keywordData = response.data.data; // Assuming your backend returns the keyword data

            // Redirect to the search page with the keyword ID
            router.push(`/?keywordId=${keywordId}`); // Pass the keyword ID instead of the keyword
        } catch (error) {
            console.error('Error fetching keyword:', error);
        }
    };

    const openModal = () => {
        setIsModalOpen(true); // Open the modal
    };

    const closeModal = () => {
        setIsModalOpen(false); // Close the modal
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
                        <div className='flex flex-col md:flex-row justify-between w-full p-4 bg-bgDarkGray rounded-lg'>
                            <div className={`bg-img w-full md:w-1/2 h-56 sm:h-custom-136 rounded p-4 ${lightMode === 'true' ? 'lightMod' : ''}`}>
                                <img src={`../../img/${path}`} className='w-full h-full object-contain' alt={title} />
                            </div>
                            <div className='info-img flex flex-col justify-between md:ms-8 md:w-1/2'>
                                <div className='flex flex-col'>
                                    <h1 className='block text-3xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>
                                        {title}
                                    </h1>
                                    <div className='flex flex-col sm:flex-row mt-4'>
                                        <div className='flex justify-between items-center bg-gray-600 px-3 py-2 rounded text-base sm:text-lg text-lightBlue'>
                                            <span className='flex items-center'>
                                                <FaImage />
                                                <p className='ms-1'>Size</p>
                                            </span>
                                            <strong className='block ml-2'>{size}</strong>
                                        </div>
                                        <div className='flex justify-between items-center bg-gray-600 mt-4 sm:mt-0 sm:ml-2 px-3 py-2 rounded text-sm sm:text-lg text-lightBlue'>
                                            <span className='flex items-center'>
                                                <RxDimensions />
                                                <p className='ms-1'>Dimensions</p>
                                            </span>
                                            <strong className='block ml-2'>{`${width} × ${height}`}</strong>
                                        </div>
                                    </div>
                                    <div className='flex flex-col sm:flex-row items-center mt-4 w-full'>
                                        <div className='showStyle w-full sm:w-auto text-sm sm:text-lg'>
                                            <div className='flex justify-between items-center bg-gray-600 rounded px-3 py-2'>
                                                <span className='flex items-center'>
                                                    <SiInstructure />
                                                    <p className='ms-1'>Style</p>
                                                </span>
                                                <span className='ml-2'>
                                                    <strong> {style}</strong>
                                                </span>
                                            </div>
                                        </div>
                                        <div className='dominantColors flex justify-between items-center w-full sm:w-auto mt-4 sm:mt-0 sm:ml-2 py-2 px-3 text-sm sm:text-lg bg-gray-600 rounded'>
                                            <span className='flex items-center'>
                                                <IoColorPaletteOutline />
                                                <p className='ms-1'>Color</p>
                                            </span>
                                            <span className='ml-2 flex items-center'>
                                                <div className='flex sm:ml-2'>
                                                    {dominantColors && dominantColors.split(',').map((color) => (
                                                        <div key={color} className='flex items-center ml-2'>
                                                            <span
                                                                className='w-4 h-4 sm:w-6 sm:h-6 rounded-full border-black border'
                                                                style={{ backgroundColor: colorHexMap[color.trim()] || '#000' }}
                                                                title={color}>
                                                            </span>
                                                        </div>
                                                    ))}
                                                </div>
                                            </span>
                                        </div>
                                    </div>
                                    <div className='showKeywords flex flex-col sm:flex-row items-start sm:items-center mt-4 py-3 px-2 text-sm sm:text-lg bg-gray-600 rounded'>
                                        <span className='flex items-center'>
                                            <FaTags className='ml-1' />
                                            <p className='ms-1'>Tag</p>
                                        </span>
                                        <div className='flex items-center flex-wrap ms-2'>
                                            {parsedKeywords.length > 4 ? ( // Check if more than 4 keywords
                                                <>
                                                    {parsedKeywords.slice(0, 4).map((kw) => ( // Show first 4 keywords
                                                        <button
                                                            key={kw.id}
                                                            onClick={() => handleKeywordClick(kw.id)}
                                                            className='text-white bg-bgDarkGray2 rounded my-1 md:my-1 px-2 py-1 mx-1'>
                                                            {kw.keyword}
                                                        </button>
                                                    ))}
                                                    <button
                                                        onClick={openModal}
                                                        className='text-white hover:text-clDarkGray2 rounded my-1 md:my-1 px-2 py-1 mx-1'>
                                                        Show more
                                                    </button>
                                                </>
                                            ) : (
                                                parsedKeywords.map((kw) => ( // Show all keywords if 4 or fewer
                                                    <button
                                                        key={kw.id}
                                                        onClick={() => handleKeywordClick(kw.id)}
                                                        className='text-white bg-bgDarkGray2 rounded my-1 md:my-1 px-2 py-1 mx-1'>
                                                        {kw.keyword}
                                                    </button>
                                                ))
                                            )}
                                        </div>
                                    </div>
                                </div>
                                <div className='flex justify-center w-full mb-2 md:mb-4'>
                                    <button className="buttonDl mt-4" type="button" onClick={handleDownload}>
                                        <span className="button__text text-xl">Download</span>
                                        <span className="button__icon">
                                            <FiDownload className='text-black text-3xl' />
                                        </span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            {isModalOpen && ( // Render the modal if it's open
                <KeywordsModal keywords={parsedKeywords} onClose={closeModal} />
            )}
        </>
    );
};

export default Downloader;