import Head from 'next/head';
import { useAuth } from '@/context/AuthContext';
import { useEffect, useState } from "react";
import { useRouter } from 'next/router';
import Card from '@/components/templates/Card';
import { apiFetch } from '@/utils/api';
import { FILE_API } from '@/utils/api/file';
import { KEYWORD_API } from '@/utils/api/keyword';
import Spinner from '@/components/modules/Spinner';
import NoImages from '@/components/modules/NoImages';
import MainLayout from '@/layouts/MainLayout';

function Index({ initialImages, initialTotalPages, initialTotalElements }) {
    const { token, role } = useAuth();
    const router = useRouter();
    const [images, setImages] = useState(initialImages);
    const [searchQuery, setSearchQuery] = useState('');
    const [submittedSearchQuery, setSubmittedSearchQuery] = useState('');
    const [spinner, setSpinner] = useState(false);
    const itemsPerPage = 50;
    const currentPage = parseInt(router.query.page) || 1;
    const [totalPages, setTotalPages] = useState(initialTotalPages);
    const [totalElements, setTotalElements] = useState(initialTotalElements);

    const fetchImages = async (keywordId = null, query = '', page = currentPage, size = itemsPerPage) => {
        setSpinner(true);
        try {
            let url;
            if (keywordId) {
                url = KEYWORD_API.LIST_IMAGES_BY_KEYWORD(keywordId, page - 1, size);
            } else if (query) {
                url = FILE_API.SEARCH_PAGINATED(page - 1, size, encodeURIComponent(query));
            } else {
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
        fetchImages('', '', page); // Fetch default images
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
                <title>PixelFreebies - Free Download PNG</title>
                <link rel="canonical" href="https://pixelfreebies.com" />
                <meta name="description"
                    content="PixelFreebies offers a vast collection of free PNG images for download. 
                Explore and find the perfect image for your project." />
                <meta name="keywords" content="Free PNG Downloads, Stock PNG Files, Free Vector PNG Files, PNG Background Images, PNG Icons and Illustrations" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />

                <meta property="og:title" content="PixelFreebies - Free PNG Images" />
                <meta property="og:description" content="Explore our extensive collection of free PNG images for your creative projects." />
                <meta property="og:image" content="/img/PixelFreebies_Banner.jpg" />
                <meta property="og:url" content="https://pixelfreebies.com" />
                <meta property="og:type" content="website" />
            </Head>
            <MainLayout
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                handleSearch={handleSearch}
                pageTitle="Free Reference for Downloading All PNG Images"
                totalElements={totalElements}
                showUploadButton={true} // or some condition based on your logic
                role={role}
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
                className="app w-full flex flex-col relative"
                mainTagClassName="main flex justify-between w-full py-4 px-4 lg:px-8"
            >

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
            </MainLayout>
        </>
    )
}

export async function getStaticProps() {
    const initialImages = []; // Fetch initial images here if needed
    const initialTotalPages = 0; // Set initial total pages
    const initialTotalElements = 0; // Set initial total elements

    return {
        props: {
            initialImages,
            initialTotalPages,
            initialTotalElements,
        },
        revalidate: 60, // Revalidate every 60 seconds
    };
}

export default Index;