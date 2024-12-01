import Head from 'next/head'; // Import Head from next/head
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { useEffect, useState } from "react";
import { useRouter } from 'next/router';
import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';
import Card from '@/components/templates/Card';
import { MdImageNotSupported, MdDelete } from "react-icons/md";
import { apiFetch } from '@/utils/api'; // Import apiFetch from utils/api

function Index() {
    const { token, username, email, clearToken, userId, role } = useAuth(); // Get user authentication details
    const router = useRouter(); // Initialize router for navigation
    const [openSelect, setOpenSelect] = useState(null); // State to manage select dropdown
    const [images, setImages] = useState([]); // State to store images from the backend
    const [searchQuery, setSearchQuery] = useState(''); // State for search query
    const [loading, setLoading] = useState(false); // State to manage loading status
    const itemsPerPage = 20; // Number of items to display per page
    const currentPage = parseInt(router.query.page) || 1; // Get the current page from the URL
    const [isSearching, setIsSearching] = useState(false); // New state for search

    const handleSelectToggle = (selectId) => {
        // Toggle the select dropdown
        if (openSelect === selectId) {
            setOpenSelect(null);
        } else {
            setOpenSelect(selectId);
        }
    };

    // Fetch images from the backend
    const fetchImages = async (query = '') => {
        setLoading(true); // Set loading state to true before starting the fetch
        try {
            const url = query
                ? `http://localhost:8080/api/v1/file/search?query=${encodeURIComponent(query)}`
                : 'http://localhost:8080/api/v1/file/list';

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
                    uploadedBy: file.uploadedBy.username,
                    categories: file.categories.map(category => category.name).join(', '),
                    lightMode: file.lightMode // Ensure this property is included
                }));

                console.log('Fetched images:', fetchedImages); // Log the fetched images
                setImages(fetchedImages); // Update state with the fetched images
            } else {
                console.error('Failed to fetch images: ', response.message); // Log an error if the response is not valid
            }
        } catch (error) {
            console.error('Failed to fetch images:', error); // Log any errors that occur during the fetch
        } finally {
            setLoading(false); // Set loading state to false after the fetch is complete
        }
    };

    // Fetch images when the component mounts or when the token changes
    useEffect(() => {
        fetchImages(); // Call the fetchImages function without a query
    }, [token]); // Only run when the token changes

    const handleDeleteImage = async (imageId) => { // Change from id to imageId
        const confirmed = window.confirm("Are you sure you want to delete this image?");
        if (confirmed) {
            try {
                const response = await apiFetch(`http://localhost:8080/api/v1/file/${imageId}`, 'DELETE', null, {
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
            const response = await apiFetch(`http://localhost:8080/api/v1/file/${imageId}`, 'PUT', updatedData, {
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
    const indexOfLastImage = currentPage * itemsPerPage; // Index of the last image on the current page
    const indexOfFirstImage = indexOfLastImage - itemsPerPage; // Index of the first image on the current page
    const currentImages = images.slice(indexOfFirstImage, indexOfLastImage); // Get the images for the current page
    const totalPages = Math.ceil(images.length / itemsPerPage); // Calculate total pages

    const handlePageChange = (page) => {
        router.push(`/?page=${page}`); // Change the page in the URL
    };

    const renderPagination = () => {
        const pagination = []; // Array to hold pagination buttons
        const maxVisiblePages = 5; // Maximum number of visible pagination buttons

        // Render pagination buttons based on total pages
        if (totalPages <= maxVisiblePages + 2) {
            for (let i = 1; i <= totalPages; i++) {
                pagination.push(
                    <button
                        key={i}
                        onClick={() => handlePageChange(i)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                        ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {i}
                    </button>
                );
            }
        } else {
            if (currentPage > 2 && currentPage < totalPages - 2) {
                pagination.push(
                    <button
                        key={1}
                        onClick={() => handlePageChange(1)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                        ${currentPage === 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {1}
                    </button>
                );
                pagination.push(<span key="dots1" className="mx-2">...</span>);
                pagination.push(
                    <button
                        key={currentPage - 1}
                        onClick={() => handlePageChange(currentPage - 1)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === currentPage - 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {currentPage - 1}
                    </button>
                );
                pagination.push(
                    <button
                        key={currentPage}
                        onClick={() => handlePageChange(currentPage)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === currentPage ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {currentPage}
                    </button>
                );
                pagination.push(
                    <button
                        key={currentPage + 1}
                        onClick={() => handlePageChange(currentPage + 1)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === currentPage + 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {currentPage + 1}
                    </button>
                );
                pagination.push(<span key="dots2" className="mx-2">...</span>);
                pagination.push(
                    <button
                        key={totalPages}
                        onClick={() => handlePageChange(totalPages)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === totalPages ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {totalPages}
                    </button>
                );
            } else if (currentPage <= 3) {
                for (let i = 1; i <= 3; i++) {
                    pagination.push(
                        <button
                            key={i}
                            onClick={() => handlePageChange(i)}
                            className={`mx-2 px-4 py-2 rounded-lg 
                                ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                            {i}
                        </button>
                    );
                }
                pagination.push(<span key="dots" className="mx-2">...</span>);
                pagination.push(
                    <button
                        key={totalPages}
                        onClick={() => handlePageChange(totalPages)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === totalPages ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {totalPages}
                    </button>
                );
            } else {
                pagination.push(
                    <button
                        key={1}
                        onClick={() => handlePageChange(1)}
                        className={`mx-2 px-4 py-2 rounded-lg 
                          ${currentPage === 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                        {1}
                    </button>
                );
                pagination.push(<span key="dots" className="mx-2">...</span>);
                for (let i = totalPages - 4; i <= totalPages; i++) {
                    pagination.push(
                        <button
                            key={i}
                            onClick={() => handlePageChange(i)}
                            className={`mx-2 px-4 py-2 rounded-lg 
                              ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}>
                            {i}
                        </button>
                    );
                }
            }
        }

        return pagination; // Return the array of pagination buttons
    };

    const handleLogout = () => {
        clearToken(); // Clear the token on logout
    };

    const handleSearch = () => {
        fetchImages(searchQuery); // Search based on the query
        setIsSearching(true); // Set the search state to true
        router.push(`/?search=${encodeURIComponent(searchQuery)}`); // Navigate to the new URL
    };

    return (
        <>
            <Head>
                <title>PixelFreebies</title>

                {/* Favicon for standard browsers */}
                <link rel="icon" type="image/png" href="/img/LOGO-icon-16x16.png" sizes="16x16" />
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
            <div className="app relative">
                <Header
                    token={token}
                    username={username}
                    email={email}
                    userId={userId}
                    handleLogout={handleLogout}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery} // Pass the state setter
                    handleSearch={handleSearch} // Pass the search handler
                />

                <div className='w-full py-12 px-8'>
                    {isSearching ? ( // Use isSearching instead of searchQuery
                        <div className='filter-result flex justify-between items-center'>
                            <div className='search-result'>
                                Searched: {searchQuery}
                            </div>
                            <button
                                onClick={() => {
                                    setSearchQuery(''); // Clear the search query
                                    setIsSearching(false); // Set the search state to false
                                    fetchImages(); // Reload all images
                                    router.push('/'); // Navigate back to the home page
                                }}
                                className='ml-4 text-2xl text-red-600 rounded-lg'>
                                <MdDelete />
                            </button>
                        </div>
                    ) : (
                        <div className='subject-text relative w-full text-center'>
                            <h1 className='relative text-6xl text-clLightPurple'>
                                {/* Free Download Reference For All PNG Images */}
                                Your Go-To Source for Free PNG Image Downloads!
                            </h1>
                        </div>
                    )}
                </div>

                <main className='main flex justify-between w-full py-8 px-2 lg:px-8'>
                    {loading ? ( // Show loading indicator while fetching images
                        <section className='loading flex justify-center w-full my-8 py-1'>
                            <div class="loader relative w-20 h-20 rounded-lg overflow-hidden bg-white"></div>
                        </section>
                    ) : currentImages.length === 0 ? ( // Check if there are no images
                        <section className='flex flex-col items-center w-full my-8'>
                            <MdImageNotSupported className='text-6xl text-gray-500' />
                            <h3 className='text-xl text-gray-500'>No images available</h3>
                        </section>
                    ) : (
                        <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                            {currentImages.map((image) => (
                                <Card key={image.id} image={image} role={role} onDelete={handleDeleteImage} onEdit={handleEditImage} />
                            ))}
                        </section>
                    )}
                </main>

                <div className="pagination flex justify-center py-4">
                    {renderPagination()} {/* Render pagination buttons */}
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