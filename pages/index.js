import Head from 'next/head';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { useEffect, useState } from "react";
import { useRouter } from 'next/router';
import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';
import Card from '@/components/templates/Card';
import { MdImageNotSupported, MdDelete } from "react-icons/md";
import { apiFetch } from '@/utils/api';
import { FILE_API } from '@/utils/api/file';
import { KEYWORD_API } from '@/utils/api/keyword';
import MouseEffect from '@/components/modules/MouseEffect';

function Index() {
    const { token, username, email, clearToken, userId, role } = useAuth(); // Get user authentication details
    const router = useRouter(); // Initialize router for navigation
    const [openSelect, setOpenSelect] = useState(null); // State to manage select dropdown
    const [images, setImages] = useState([]); // State to store images from the backend
    const [searchQuery, setSearchQuery] = useState(''); // State for search query
    const [submittedSearchQuery, setSubmittedSearchQuery] = useState(''); // New state for submitted search query
    const [loading, setLoading] = useState(false); // State to manage loading status
    const itemsPerPage = 50; // Number of items to display per page
    const currentPage = parseInt(router.query.page) || 1; // Get the current page from the URL
    const [isSearching, setIsSearching] = useState(false); // New state for search
    const [totalPages, setTotalPages] = useState(0); // State to store total pages

    const handleSelectToggle = (selectId) => {
        // Toggle the select dropdown
        if (openSelect === selectId) {
            setOpenSelect(null);
        } else {
            setOpenSelect(selectId);
        }
    };

    // Fetch images from the backend
    const fetchImages = async (keywordId = null, page = currentPage, size = itemsPerPage) => {
        setLoading(true); // Set loading state to true before starting the fetch
        try {
            let url;
            if (keywordId) {
                url = `${KEYWORD_API.LIST_IMAGES_BY_KEYWORD(keywordId, page -1, size)}`;
            } else {
                url = FILE_API.LIST_IMAGES_PAGINATED(page - 1, size);
            }

            console.log('Fetching images from URL:', url); // Log the URL being fetched

            const response = await apiFetch(url, 'GET', null, {});
            console.log('Response received:', response); // Log the response received

            if (response.flag && response.data) {
                const fetchedImages = response.data.map((file) => ({
                    id: file.id,
                    title: file.fileTitle,
                    path: file.filePath,
                    contentType: file.contentType,
                    size: file.size,
                    width: file.width,
                    height: file.height,
                    style: file.style,
                    dominantColors: file.dominantColors,
                    uploadedBy: file.uploadedBy.username,
                    lightMode: file.lightMode,
                    source: file.source,
                    // Convert keywords to a comma-separated string
                    keywords: JSON.stringify(file.keywords)
                }));

                console.log('Fetched images:', fetchedImages); // Log the fetched images
                setImages(fetchedImages); // Update state with the fetched images
                setTotalPages(response.totalPages); // Set total pages from the response
            } else {
                console.error('Failed to fetch images: ', response.message); // Log an error if the response is not valid
            }
        } catch (error) {
            console.error('Failed to fetch images:', error); // Log any errors that occur during the fetch
        } finally {
            setLoading(false); // Set loading state to false after the fetch is complete
        }
    };

    // Fetch images when the component mounts or when the token, currentPage, or keywordId changes
    useEffect(() => {
        const keywordId = router.query.keywordId; // Get keywordId from the URL
        fetchImages(keywordId ? keywordId : '', currentPage); // Call the fetchImages function with the keywordId if present
    }, [token, currentPage, router.query.keywordId]); // Only run when the token, currentPage, or keywordId changes

    const handleDeleteImage = async (imageId) => {
        const confirmed = window.confirm("Are you sure you want to delete this image?");
        if (confirmed) {
            try {
                const response = await apiFetch(FILE_API.DELETE(imageId), 'DELETE', null, {
                    headers: {
                        'Authorization': `Bearer ${token}`, // Add your token if needed
                    }
                });

                if (response) {
                    // Update the state to remove the deleted image
                    setImages((prevImages) => prevImages.filter(image => image.id !== imageId));
                } else {
                    console.error('Failed to delete image');
                }
            } catch (error) {
                console.error('Error deleting image:', error);
            }
        }
    };

    const handleEditImage = async (imageId, updatedData) => {
        try {
            const response = await apiFetch(FILE_API.UPDATE(imageId), 'PUT', updatedData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                }
            });

            if (response) {
                // Update the images state with the edited image
                setImages((prevImages) => prevImages.map(image =>
                    image.id === imageId ? { ...image, ...updatedData } : image
                ));
            } else {
                console.error('Failed to edit image');
            }
        } catch (error) {
            console.error('Error editing image:', error);
        }
    };

    // Pagination logic
    const handlePageChange = (page) => {
        router.push(`/?page=${page}`); // Change the page in the URL
        fetchImages(searchQuery, page); // Fetch images for the new page
    };

    const renderPagination = () => {
        const pagination = []; // Array to hold pagination buttons
        const maxVisiblePages = 5; // Maximum number of visible pagination buttons

        // Calculate the start and end page numbers
        let startPage, endPage;
        if (totalPages <= maxVisiblePages) {
            // If total pages are less than or equal to maxVisiblePages, show all pages
            startPage = 1;
            endPage = totalPages;
        } else {
            // Calculate start and end page based on current page
            const middlePage = Math.ceil(maxVisiblePages / 2);
            if (currentPage <= middlePage) {
                startPage = 1;
                endPage = maxVisiblePages;
            } else if (currentPage + middlePage - 1 >= totalPages) {
                startPage = totalPages - maxVisiblePages + 1;
                endPage = totalPages;
            } else {
                startPage = currentPage - middlePage + 1;
                endPage = currentPage + middlePage - 1;
            }
        }

        // Add "First" button
        if (startPage > 1) {
            pagination.push(
                <button key="first" onClick={() => handlePageChange(1)} className="mx-2 px-4 py-2 rounded-lg bg-bgDarkGray hover:bg-bgDarkGray2">
                    First
                </button>
            );
            if (startPage > 2) {
                pagination.push(<span key="ellipsis-start">...</span>);
            }
        }

        // Render pagination buttons
        for (let i = startPage; i <= endPage; i++) {
            pagination.push(
                <button
                    key={i}
                    onClick={() => handlePageChange(i)}
                    className={`mx-2 px-4 py-2 rounded-lg 
                    ${currentPage === i ? 'bg-gradient-to-t from-bgLightPurple to-bgPurple text-white' : 'bg-bgDarkGray hover:bg-bgDarkGray2'}`}>
                    {i}
                </button>
            );
        }

        // Add "Last" button
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.push(<span key="ellipsis-end">...</span>);
            }
            pagination.push(
                <button key="last" onClick={() => handlePageChange(totalPages)}
                    className="mx-2 px-4 py-2 rounded-lg bg-bgDarkGray hover:bg-bgDarkGray2">
                    Last
                </button>
            );
        }

        return pagination;
    };

    const handleLogout = () => {
        clearToken();
    };

    const handleSearch = () => {
        if (!searchQuery.trim()) {
            return;
        }
        setSubmittedSearchQuery(searchQuery);
        fetchImages(searchQuery, 1);
        setIsSearching(true);
        router.push(`/?search=${encodeURIComponent(searchQuery)}`);
    };

    return (
        <>
            <Head>
                <title>PixelFreebies</title>
                <link rel="canonical" href="https://pixelfreebies.com" />
                <meta name="description"
                    content="PixelFreebies offers a vast collection of free PNG images for download. 
                Explore and find the perfect image for your project." />
                {/* <meta name="keywords" content="free images, PNG images, download images, PixelFreebies, stock photos" /> */}
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <meta name="robots" content="index, follow" />

                <meta property="og:title" content="PixelFreebies - Free PNG Images" />
                <meta property="og:description" content="Explore our extensive collection of free PNG images for your creative projects." />
                <meta property="og:image" content="URL_to_a_representative_image.jpg" />
                <meta property="og:url" content="https://pixelfreebies.com" />
                <meta property="og:type" content="website" />

                {/* Favicon for standard browsers */}
                <link rel="icon" type="image/png" href="/img/LOGO.png" sizes="16x16" />
                <link rel="icon" type="image/png" href="/img/LOGO-icon-32x32.png" sizes="32x32" />
                <link rel="icon" type="image/png" href="/img/LOGO-icon-48x48.png" sizes="48x48" />
                <link rel="icon" type="image/png" href="/img/LOGO-icon-192x192.png" sizes="192x192" />
                <link rel="icon" type="image/png" href="/img/LOGO-icon-512x512.png" sizes="512x512" />

                {/* Apple Touch Icon for iOS devices */}
                <link rel="apple-touch-icon" href="/img/LOGO-icon-180x180.png" sizes="180x180" />
                <link rel="apple-touch-icon" href="/img/LOGO-icon-152x152.png" sizes="152x152" />
                <link rel="apple-touch-icon" href="/img/LOGO-icon-120x120.png" sizes="120x120" />

                {/* Android Chrome Icon */}
                <link rel="icon" type="image/png" href="/img/LOGO-icon-192x192.png" sizes="192x192" />

                {/* Microsoft Tiles for Windows */}
                <meta name="msapplication-TileColor" content="#ffffff" />
                <meta name="msapplication-TileImage" content="/img/LOGO-icon-270x270.png" />
            </Head>
            <div className="app flex flex-col min-h-screen relative"> 
                <Header
                    token={token}
                    username={username}
                    email={email}
                    userId={userId}
                    handleLogout={handleLogout}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    handleSearch={handleSearch}
                />

                <div className='w-full py-12 px-8 flex-grow'>
                    {isSearching ? (
                        <div className='filter-result flex justify-center items-center'>
                            <div className='search-result flex items-center rounded p-2 bg-gradient-to-t from-bgLightPurple to-bgPurple'>
                                Result for:
                                <span className='ml-2'>
                                    {submittedSearchQuery}
                                </span>
                                <button
                                    onClick={() => {
                                        setSearchQuery('');
                                        setIsSearching(false);
                                        fetchImages();
                                        router.push('/');
                                    }}
                                    className='ml-2 text-xl text-white hover:text-red-600 rounded-lg'>
                                    <MdDelete />
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className='subject-text relative w-full text-center'>
                            <h1 className='relative text-2xl md:text-4xl lg:text-6xl text-clLightPurple'>
                                Free Reference for Downloading All PNG Images
                            </h1>
                        </div>
                    )}
                </div>

                <main className='main flex justify-between w-full py-8 px-4 lg:px-8'>
                    {loading ? ( // Show loading indicator while fetching images
                        <section className='loading flex justify-center w-full my-8 py-1'>
                            <div className="loader relative w-20 h-20 rounded-lg overflow-hidden bg-white"></div>
                        </section>
                    ) : images.length === 0 ? ( // Check if there are no images
                        <section className='flex flex-col items-center w-full my-8'>
                            <MdImageNotSupported className='text-6xl text-gray-500' />
                            <h3 className='text-xl text-gray-500'>No images available</h3>
                        </section>
                    ) : (
                        <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                            {images.map((image) => (
                                <Card key={image.id} image={image} role={role} onDelete={handleDeleteImage} onEdit={handleEditImage} />
                            ))}
                        </section>
                    )}
                </main>

                <div className="pagination flex justify-center py-4">
                    {renderPagination()}
                </div>

                <Footer />

                {/* Conditionally render the upload link based on the user's role */}
                {role === 'ROLE_MASTER' && (
                    <Link
                        href={'/uploadImage'}
                        className='fixed right-3 bottom-3
                            w-10 h-10 p-6 flex justify-center items-center
                            bg-blue-700 text-white text-2xl outline-none
                            rounded-full cursor-pointer'>
                        +
                    </Link>
                )}
            </div>
        </>
    );
}

export default Index;