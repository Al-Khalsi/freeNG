import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { useEffect, useState } from "react";
import { useRouter } from 'next/router';
import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';
import Card from '@/components/templates/Card';
import { apiFetch } from '@/utils/api'; // Import apiFetch from utils/api

function Index() {
    const { token, username, email, clearToken, userId } = useAuth(); // Get user authentication details
    const router = useRouter(); // Initialize router for navigation
    const [openSelect, setOpenSelect] = useState(null); // State to manage select dropdown
    const [images, setImages] = useState([]); // State to store images from the backend
    const [searchQuery, setSearchQuery] = useState(''); // State for search query
    const [loading, setLoading] = useState(false); // State to manage loading status
    const itemsPerPage = 20; // Number of items to display per page
    const currentPage = parseInt(router.query.page) || 1; // Get the current page from the URL

    const handleSelectToggle = (selectId) => {
        // Toggle the select dropdown
        if (openSelect === selectId) {
            setOpenSelect(null);
        } else {
            setOpenSelect(selectId);
        }
    };

    // Fetch images from the backend
    useEffect(() => {
        const fetchImages = async () => {
            setLoading(true); // Set loading state to true before starting the fetch
            try {
                // Determine the URL based on the search query
                const url = searchQuery
                    ? `http://localhost:8080/api/v1/file/search?query=${encodeURIComponent(searchQuery)}`
                    : 'http://localhost:8080/api/v1/file/list';

                console.log('Fetching images from URL:', url); // Log the URL being fetched

                // Call the apiFetch function to get the images
                const response = await apiFetch(url, 'GET', null, {});
                console.log('Response received:', response); // Log the response received

                // Check if the response contains valid data
                if (response.flag && response.data) {
                    // Map the response data to the desired format
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

        fetchImages(); // Call the fetchImages function
    }, [token, searchQuery]); // Dependency array to re-run effect when token or searchQuery changes

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

    // Function to handle search action
    const handleSearch = () => {
        // Update the router to include the search query
        router.push(`/?search=${encodeURIComponent(searchQuery)}`);
    };

    return (
        <>
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

                <main className='main flex justify-between w-full py-8 px-2 lg:px-8'>
                    <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                        {loading ? ( // Show loading indicator while fetching images
                            <div>Loading...</div>
                        ) : (
                            currentImages.map((image) => (
                                <Card key={image.id} image={image} />
                            ))
                        )}
                    </section>
                </main>

                <div className="pagination flex justify-center py-4">
                    {renderPagination()} {/* Render pagination buttons */}
                </div>

                <Footer />

                <button
                    className='fixed right-3 bottom-3
                        w-10 h-10 p-6 flex justify-center items-center
                        bg-blue-700 text-white text-2xl outline-none
                        rounded-full cursor-pointer'>
                    <Link href={'/uploadImage'}>+</Link>
                </button>
            </div>
        </>
    );
}

export default Index;