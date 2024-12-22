import Head from 'next/head';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { useEffect, useState } from "react";
import { useRouter } from 'next/router';
import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';
import Card from '@/components/templates/Card';
import { MdDelete } from "react-icons/md";
import { apiFetch } from '@/utils/api';
import { FILE_API } from '@/utils/api/file';
import { KEYWORD_API } from '@/utils/api/keyword';
import MouseEffect from '@/components/modules/MouseEffect';
import Spinner from '@/components/modules/Spinner';
import NoImages from '@/components/modules/NoImages';

function Index() {
    const { token, username, email, clearToken, userId, role } = useAuth();
    const router = useRouter();
    const [openSelect, setOpenSelect] = useState(null);
    const [images, setImages] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [submittedSearchQuery, setSubmittedSearchQuery] = useState('');
    const [spinner, setSpinner] = useState(false);
    const itemsPerPage = 50;
    const currentPage = parseInt(router.query.page) || 1;
    const [isSearching, setIsSearching] = useState(false);
    const [totalPages, setTotalPages] = useState(0);

    const handleSelectToggle = (selectId) => {
        if (openSelect === selectId) {
            setOpenSelect(null);
        } else {
            setOpenSelect(selectId);
        }
    };

    const fetchImages = async (keywordId = null, query = '', page = currentPage, size = itemsPerPage) => {
        setSpinner(true);
        try {
            let url;
            if (keywordId) {
                url = KEYWORD_API.LIST_IMAGES_BY_KEYWORD(keywordId, page - 1, size);
            } else if (query) {
                url = FILE_API.SEARCH_PAGINATED(page - 1, size, encodeURIComponent(query));
            }
            else {
                url = FILE_API.LIST_IMAGES_PAGINATED(page - 1, size);
            }

            const response = await apiFetch(url, 'GET', null, {});

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
                    keywords: JSON.stringify(file.keywords)
                }));

                setImages(fetchedImages); // Update state with the fetched images
                setTotalPages(response.totalPages); // Set total pages from the response
            } else {
                console.error('Failed to fetch images: ', response.message);
            }
        } catch (error) {
            console.error('Failed to fetch images:', error);
            router.push('/500')
        } finally {
            setSpinner(false);
        }
    };

    useEffect(() => {
        const keywordId = router.query.keywordId; // Get keywordId from the URL
        const keywordName = router.query.keywordName; // Get keywordName from the URL
        const searchQueryFromUrl = router.query.search; // Get search query from the URL

        if (keywordId) {
            fetchImages(keywordId, '', currentPage); // Fetch images based on keywordId
            setSubmittedSearchQuery(keywordName); // Set submittedSearchQuery to the keyword name
        } else if (searchQueryFromUrl && searchQueryFromUrl.trim() !== '') {
            fetchImages(null, searchQueryFromUrl, currentPage);
            setSubmittedSearchQuery(searchQueryFromUrl); // Set submittedSearchQuery to the search query
        } else {
            fetchImages('', '', currentPage, itemsPerPage);
            setSubmittedSearchQuery(''); // Reset submittedSearchQuery
        }
    }, [token, currentPage, router.query.keywordId, router.query.keywordName, router.query.search]);

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
        // Push the new page to the URL
        router.push(`/?page=${page}`);

        // Fetch images based on the current search query or default
        if (searchQuery) {
            fetchImages(null, searchQuery, page); // If there's a search query, use it
        } else {
            fetchImages('', '', page); // Otherwise, fetch default images
        }
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
        const trimmedSearchQuery = searchQuery.trim();
        if (!trimmedSearchQuery) {
            return; // Do not proceed if the search query is empty
        }
        setSubmittedSearchQuery(trimmedSearchQuery);
        fetchImages(trimmedSearchQuery, 1);
        setIsSearching(true);
        router.push(`/?search=${encodeURIComponent(trimmedSearchQuery)}`);
    };

    return (
        <>
            <Head>
                <title>PixelFreebies - Free Download PNG</title>
                <link rel="canonical" href="https://pixelfreebies.com" />
                <meta name="description"
                    content="PixelFreebies offers a vast collection of free PNG images for download. 
                Explore and find the perfect image for your project." />
                <meta name="keywords" content="Free PNG Downloads, Stock PNG Files, Free Vector PNG Files, PNG Background Images, PNG Icons and Illustrations" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <meta name="robots" content="index, follow" />

                <meta property="og:title" content="PixelFreebies - Free PNG Images" />
                <meta property="og:description" content="Explore our extensive collection of free PNG images for your creative projects." />
                <meta property="og:image" content="/img/PixelFreebies_Banner.jpg" />
                <meta property="og:url" content="https://pixelfreebies.com" />
                <meta property="og:type" content="website" />
            </Head>
            <div className="app w-full h-full flex flex-col min-h-screen relative">
                <Header
                    token={token}
                    username={username}
                    email={email}
                    userId={userId}
                    handleLogout={handleLogout}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    handleSearch={handleSearch} />

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
                        <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 
                        md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                            {images.map((image) => (
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
    )
}

export default Index;