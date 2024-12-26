import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { apiFetch } from '@/utils/api';
import { FILE_API } from '@/utils/api/file';
import Card from '@/components/templates/Card';
import Spinner from '@/components/modules/Spinner';
import NoImages from '@/components/modules/NoImages';
import Footer from '@/components/templates/Footer';
import Header from '@/components/templates/Header';
import { useAuth } from '@/context/AuthContext';
import { MdDelete } from "react-icons/md";
import Link from 'next/link';

function SearchPage() {
    const router = useRouter();
    const { query } = router.query;
    const [images, setImages] = useState([]);
    const [spinner, setSpinner] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const { token, username, email, clearToken, userId, role } = useAuth();
    const [submittedSearchQuery, setSubmittedSearchQuery] = useState('');
    const [isSearching, setIsSearching] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const fetchImages = async (searchQuery, page = 1) => {
        setSpinner(true);
        try {
            const response = await apiFetch(FILE_API.SEARCH_PAGINATED(page - 1, 50, encodeURIComponent(searchQuery)), 'GET');
            if (response.flag && response.data) {
                const fetchedImages = response.data.map(file => ({
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
                    keywords: JSON.stringify(file.keywords)
                }));
                setImages(fetchedImages);
                setTotalPages(response.totalPages);
                setTotalElements(response.totalElements);
            } else {
                console.error('Failed to fetch images: ', response.message);
            }
        } catch (error) {
            console.error('Failed to fetch images:', error);
        } finally {
            setSpinner(false);
        }
    };

    useEffect(() => {
        if (query) {
            setSubmittedSearchQuery(query); // Set the submitted search query
            fetchImages(query, currentPage); // Fetch images with the current page
        }
    }, [query, currentPage]);

    const handleSearch = () => {
        const trimmedSearchQuery = searchQuery.trim();
        if (!trimmedSearchQuery) {
            return; // Do not proceed if the search query is empty
        }
        setSubmittedSearchQuery(trimmedSearchQuery);
        router.push(`/search?query=${encodeURIComponent(trimmedSearchQuery)}`);
    };

    const handleClearSearch = () => {
        setSearchQuery('');
        setIsSearching(false);
        fetchImages('');
        router.push('/'); // Navigate back to the home page
    };

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

    // Pagination logic
    const handlePageChange = (page) => {
        setCurrentPage(page);
        fetchImages(submittedSearchQuery, page); // Fetch images for the new page
    };

    const renderPagination = () => {
        const pagination = []; // Array to hold pagination buttons
        const maxVisiblePages = 5; // Maximum number of visible pagination buttons

        let startPage, endPage;
        if (totalPages <= maxVisiblePages) {
            startPage = 1;
            endPage = totalPages;
        } else {
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

    return (
        <>
            <Head>
                <title>{submittedSearchQuery} - Free PNG Images</title>
                <link rel="canonical" href={`https://pixelfreebies.com/search/${query}`} />
                <meta name="description"
                    content={`${submittedSearchQuery} image png collection for free download at PixelFreebies`} />
                <meta name="keywords" content={`
                    ${query} png,
                    ${query} png free,
                    ${query} png download,
                    ${query} png images,
                    ${query} free png
                `} />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <meta name="robots" content="index, follow" />

                <meta property="og:title" content={`${query} - Free PNG Images`} />
                <meta property="og:description"
                    content={`${query} image png collection for free download at PixelFreebies`} />
                <meta property="og:image" content="/img/PixelFreebies_Banner.jpg" />
                <meta property="og:url" content="https://pixelfreebies.com" />
                <meta property="og:type" content="website" />
            </Head>
            <div className='w-full h-full flex flex-col min-h-screen relative'>
                <Header
                    token={token}
                    username={username}
                    email={email}
                    userId={userId}
                    handleLogout={clearToken}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    handleSearch={handleSearch}
                />
                <div className='w-full py-12 px-8 flex-grow'>
                    {isSearching || submittedSearchQuery ? (
                        <div className='filter-result flex justify-center items-center'>
                            <h3 className='search-result flex items-center rounded p-2 
                            bg-gradient-to-t from-bgLightPurple to-bgPurple'>
                                {router.query.keywordId ? 'Result tag for' : 'Result search for'}
                                :
                                <span className='ml-2'>
                                    {submittedSearchQuery || (router.query.keywordName ? router.query.keywordName : '')}
                                </span>
                                <span className='ml-2 text-white'>
                                    ({totalElements})
                                </span>
                                <button
                                    onClick={handleClearSearch}
                                    className='ml-2 text-xl text-white hover:text-red-600 rounded-lg'>
                                    <MdDelete />
                                </button>
                            </h3>
                        </div>
                    ) : (
                        <div className='subject-text relative w-full text-center'>
                            <h1 className='relative text-2xl md:text-4xl lg:text-6xl text-clLightPurple'>
                                Free Reference for Downloading All PNG Images
                            </h1>
                        </div>
                    )}
                </div>
                <main className='main flex justify-between w-full py-4 px-4 lg:px-8'>
                    {spinner ? (
                        <Spinner />
                    ) : images.length === 0 ? (
                        <NoImages />
                    ) : (
                        <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                            {images.map(image => (
                                <Card key={image.id} image={image} role={role} onDelete={handleDeleteImage} />
                            ))}
                        </section>
                    )}
                </main>
                <div className="pagination flex justify-center py-4">
                    {renderPagination()}
                </div>
                <Footer />

                {role === 'ROLE_MASTER' && (
                    <Link
                        href={'/upload'}
                        className='fixed right-3 bottom-3
                            w-10 h-10 p-6 flex justify-center items-center
                            bg-gradient-to-t from-bgLightPurple to-bgPurple text-white text-2xl 
                            outline-none rounded-lg cursor-pointer'>
                        +
                    </Link>
                )}

            </div>
        </>
    );
}

export default SearchPage;