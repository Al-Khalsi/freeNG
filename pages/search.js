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

function SearchPage() {
    const router = useRouter();
    const { query } = router.query;
    const [images, setImages] = useState([]);
    const [spinner, setSpinner] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const { token, username, email, clearToken, userId, role } = useAuth();
    const [submittedSearchQuery, setSubmittedSearchQuery] = useState('');
    const [isSearching, setIsSearching] = useState(false);

    const fetchImages = async (searchQuery) => {
        setSpinner(true);
        try {
            const response = await apiFetch(FILE_API.SEARCH_PAGINATED(0, 50, encodeURIComponent(searchQuery)), 'GET');
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
            fetchImages(query);
        }
    }, [query]);

    const handleSearch = () => {
        if (searchQuery.trim()) {
            router.push(`/?search=${encodeURIComponent(searchQuery)}`);
        }
    };

    const handleClearSearch = () => {
        setSearchQuery('');
        setIsSearching(false);
        fetchImages('');
        router.push('/'); // Navigate back to the home page
    };

    return (
        <>
            <Head>
                <title>{query} - PNG Image</title>
                <link rel="canonical" href={`https://pixelfreebies.com/search/${query}`} />
                <meta name="description" content={`${query}`} />
                <meta name="keywords" content={query} />
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
                {spinner ? (
                    <Spinner />
                ) : images.length === 0 ? (
                    <NoImages />
                ) : (
                    <section className='grid gap-6 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
                        {images.map(image => (
                            <Card key={image.id} image={image} />
                        ))}
                    </section>
                )}
                <Footer />
            </div>
        </>
    );
}

export default SearchPage;