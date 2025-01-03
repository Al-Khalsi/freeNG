import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import { SiInstructure } from "react-icons/si";
import { IoColorPaletteOutline } from "react-icons/io5";
import { FiDownload } from "react-icons/fi";
import { IoIosLink } from "react-icons/io";
import { FaTags } from "react-icons/fa6";
import axios from 'axios';
import KeywordsModal from '@/components/templates/KeywordsModal';
import { FILE_API } from "@/utils/api/file";
import { KEYWORD_API } from "@/utils/api/keyword";
import Header from '@/components/templates/Header';
import { useAuth } from '@/context/AuthContext';
import Footer from '@/components/templates/Footer';
import { useImageContext } from '@/context/ImageContext';

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

const Downloader = () => {
    const router = useRouter();
    const { imageData } = useImageContext();
    const { id: fileId, title, path, size, width, height, lightMode, style, dominantColors, source, keywords } = imageData || {};
    const { token, username, email, clearToken, userId, role } = useAuth();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [parsedKeywords, setParsedKeywords] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        if (keywords) {
            try {
                const keywordArray = JSON.parse(keywords);
                setParsedKeywords(keywordArray);
            } catch (error) {
                console.error('Error parsing keywords:', error);
            }
        }
    }, [keywords]);

    const handleDownload = async () => {
        if (!fileId) {
            console.warn('File ID is not available for download.');
            return;
        }

        try {
            const response = await axios.get(`${FILE_API.DOWNLOAD(fileId)}`);
            // The final S3 URL after backend redirect with 302 response code
            const s3Url = response.request.responseURL;

            // Create a temporary link and click it
            const link = document.createElement('a');
            link.href = s3Url;
            /* opens in new tab : نوموخوام برای دانلود صفحه جدیدی باز شه پس اینو کامنت میکنم */
            // link.target = '_blank';
            link.rel = 'noopener noreferrer';  // Security best practice
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

        } catch (error) {
            console.error('Error downloading the file:', error);
            router.push('/500');
        }
    };

    const handleKeywordClick = async (keywordId, keywordName) => {
        try {
            const response = await axios.get(`${KEYWORD_API.LIST_IMAGES_BY_KEYWORD(keywordId, 0, 50)}`);
            await router.push(`/?keywordId=${keywordId}&keywordName=${encodeURIComponent(keywordName)}`);
        } catch (error) {
            console.error('Error fetching keyword:', error);
        }
    };

    const openModal = () => {
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
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
        <>
            <Head>
                <title>{title} - Free Download</title>
                <link rel="canonical" href={`https://pixelfreebies.com/download/${title}`} />
                <meta name="description"
                    content={`${title} is a free png image with dimensions ${width}x${height} and size ${size} in ${style} style available for free.`} />
                <meta name="keywords" content={parsedKeywords.map(kw => kw.keyword).join(', ')} />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />

                <meta property="og:title" content={title} />
                <meta property="og:description" content={`Download ${title} - A beautiful PNG image`} />
                <meta property="og:image" content={`${path}`} />
                <meta property="og:url" content={`https://pixelfreebies.com/download/${title}`} />
                <meta property="og:type" content="website" />
            </Head>
            <div className={`downloaderPage w-full`}>
                <Header
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    handleSearch={handleSearch}
                />
                <main className='w-full h-auto flex-auto'>
                    <div className='flex justify-between w-full py-12 px-4 lg:px-8'>
                        <div className='flex flex-col md:flex-row justify-between w-full p-4 bg-bgDarkGray rounded-lg'>
                            <div className={`bg-img w-full md:w-1/2 h-56 md:h-custom-136 rounded p-4 ${lightMode === 'true' ? 'lightMod' : ''}`}>
                                <img src={`${path}`} className='w-full h-full object-contain' alt={title} />
                            </div>
                            <div className='info-img flex flex-col justify-between md:mx-8 md:w-1/2'>
                                <div className='flex flex-col'>
                                    <h1 className='block text-3xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>
                                        {title}
                                    </h1>
                                    <div className='flex flex-col md:flex-row mt-4'>
                                        <div className='flex justify-between items-center bg-gray-600 px-3 py-2 rounded text-base md:text-lg text-lightBlue'>
                                            <span className='flex items-center'>
                                                <FaImage />
                                                <p className='ms-1'>Size</p>
                                            </span>
                                            <strong className='block ml-2'>{size}</strong>
                                        </div>
                                        <div className='flex justify-between items-center bg-gray-600 mt-4 md:mt-0 md:ml-2 px-3 py-2 rounded text-sm md:text-lg text-lightBlue'>
                                            <span className='flex items-center'>
                                                <RxDimensions />
                                                <p className='ms-1'>Dimensions</p>
                                            </span>
                                            <strong className='block ml-2'>{`${width} × ${height}`}</strong>
                                        </div>
                                    </div>
                                    <div className='flex flex-col md:flex-row items-center mt-4 w-full'>
                                        <div className='showStyle w-full md:w-auto text-sm md:text-lg'>
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
                                        <div className='dominantColors flex justify-between items-center w-full md:w-auto mt-4 md:mt-0 md:ml-2 py-2 px-3 text-sm md:text-lg bg-gray-600 rounded'>
                                            <span className='flex items-center'>
                                                <IoColorPaletteOutline />
                                                <p className='ms-1'>Color</p>
                                            </span>
                                            <span className='ml-2 flex items-center'>
                                                <div className='flex md:ml-2'>
                                                    {dominantColors && dominantColors.split(',').map((colorName) => {
                                                        // Find the color object by name
                                                        const colorObj = colors.find(color => color.name.trim() === colorName.trim());
                                                        return (
                                                            <div key={colorName} className='flex items-center ml-2'>
                                                                <span
                                                                    className='w-4 h-4 md:w-6 md:h-6 rounded-full border-black border'
                                                                    style={{ backgroundColor: colorObj ? colorObj.hex : '#000' }} // Use hex from the color object
                                                                    title={colorName}>
                                                                </span>
                                                            </div>
                                                        );
                                                    })}
                                                </div>
                                            </span>
                                        </div>
                                    </div>
                                    <div className='flex flex-col md:flex-row items-center mt-4 w-full'>
                                        <div className='sourceLink flex justify-between items-center w-full md:w-auto md:mt-0 py-2 px-3 text-sm md:text-lg bg-gray-600 rounded'>
                                            <span className='flex items-center'>
                                                <IoIosLink />
                                                <p className='ms-1'>Source</p>
                                            </span>
                                            <p className='ml-2'>
                                                {source === "" ? "PixelFreebies" : source}
                                            </p>
                                        </div>
                                    </div>
                                    <div className='showKeywords flex flex-col items-start mt-4 py-3 px-2 text-sm md:text-lg bg-gray-600 rounded'>
                                        <span className='flex items-center'>
                                            <FaTags className='ml-1' />
                                            <p className='ms-1'>Tag</p>
                                        </span>
                                        <div className='flex items-center flex-wrap ms-2'>
                                            {parsedKeywords.length > 20 ? (
                                                <>
                                                    {parsedKeywords.slice(0, 20).map((kw) => (
                                                        <button
                                                            key={kw.id}
                                                            onClick={() => handleKeywordClick(kw.id, kw.keyword)}
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
                                                parsedKeywords.map((kw) => (
                                                    <button
                                                        key={kw.id}
                                                        onClick={() => handleKeywordClick(kw.id, kw.keyword)}
                                                        className='text-white bg-bgDarkGray2 rounded my-1 md:my-1 px-2 py-1 mx-1'>
                                                        {kw.keyword}
                                                    </button>
                                                ))
                                            )}
                                        </div>
                                    </div>
                                </div>
                                <div className='flex justify-center w-full mb-2 md:mb-4'>
                                    <button className="buttonDl relative flex justify-between items-center
                                    w-full h-16 mt-4 bg-bgDarkGray2 rounded overflow-hidden duration-300
                                    cursor-pointer" type="button" onClick={handleDownload}>
                                        <span className="button__text w-full text-white text-lg
                                        md:text-xl font-semibold duration-300">Download</span>
                                        <span className="button__icon
                                        flex justify-center items-center h-full duration-300">
                                            <FiDownload className='text-black text-3xl' />
                                        </span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
                <Footer />
            </div>

            {isModalOpen && (
                <KeywordsModal
                    keywords={parsedKeywords}
                    onClose={closeModal}
                    onKeywordClick={handleKeywordClick}
                />
            )}
        </>
    );
};

export default Downloader;