// pages/index.js
import Link from 'next/link';
import { useAuth } from '../context/AuthContext';
import { useEffect, useState } from "react";
import Images from "../data/db.json";
import { useRouter } from 'next/router';
import Header from '@/components/templates/Header';
import Aside from '@/components/templates/Aside';
import { apiFetch } from '../utils/api'; // Import the apiFetch function
import Footer from '@/components/templates/Footer';
import MouseEffect from '@/components/modules/MouseEffect';
import Card from '@/components/templates/Card';

function Index() {
  const { token, username, email, clearToken, userId } = useAuth(); // Destructure token and username from Auth context
  const router = useRouter();
  const [openSelect, setOpenSelect] = useState(null);
  const [images, setImages] = useState([]); // State to store images from the backend
  const itemsPerPage = 20;
  const currentPage = parseInt(router.query.page) || 1;

  const options1 = ['Animation', 'Character', 'Technology', 'Nature', 'Game', 'Space'];
  const options2 = ['Cartoon', 'Upset', 'Mobile', 'sea', 'Console', 'Star'];
  const options3 = ['Logo', '3D', 'Pixel', 'Anime'];
  const options4 = ['Black', 'Blue', 'Red', 'Green', 'Purple', 'White'];

  const handleSelectToggle = (selectId) => {
    if (openSelect === selectId) {
      setOpenSelect(null);
    } else {
      setOpenSelect(selectId);
    }
  };

  // Fetch images from the backend
  useEffect(() => {
    const fetchImages = async () => {
      try {
        const response = await apiFetch('/api/images', 'GET', null, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        setImages(response.images); // Assuming the response structure has an images array
      } catch (error) {
        console.error('Failed to fetch images:', error);
      }
    };

    fetchImages();
  }, [token]);

  const indexOfLastImage = currentPage * itemsPerPage;
  const indexOfFirstImage = indexOfLastImage - itemsPerPage;
  const currentImages = Images.Image.slice(indexOfFirstImage, indexOfLastImage);
  const totalPages = Math.ceil(Images.Image.length / itemsPerPage);

  const handlePageChange = (page) => {
    router.push(`/?page=${page}`);
  };


  const renderPagination = () => {
    const pagination = [];
    const maxVisiblePages = 5;

    if (totalPages <= maxVisiblePages + 2) {

      for (let i = 1; i <= totalPages; i++) {
        pagination.push(
          <button
            key={i}
            onClick={() => handlePageChange(i)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {i}
          </button>
        );
      }
    } else {
      // اگر در صفحات میانی هستیم
      if (currentPage > 2 && currentPage < totalPages - 2) {
        pagination.push(
          <button
            key={1}
            onClick={() => handlePageChange(1)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {1}
          </button>
        );
        pagination.push(<span key="dots1" className="mx-2">...</span>);
        pagination.push(
          <button
            key={currentPage - 1}
            onClick={() => handlePageChange(currentPage - 1)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === currentPage - 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {currentPage - 1}
          </button>
        );
        pagination.push(
          <button
            key={currentPage}
            onClick={() => handlePageChange(currentPage)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === currentPage ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {currentPage}
          </button>
        );
        pagination.push(
          <button
            key={currentPage + 1}
            onClick={() => handlePageChange(currentPage + 1)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === currentPage + 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {currentPage + 1}
          </button>
        );
        pagination.push(<span key="dots2" className="mx-2">...</span>);
        pagination.push(
          <button
            key={totalPages}
            onClick={() => handlePageChange(totalPages)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === totalPages ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {totalPages}
          </button>
        );
      } else if (currentPage <= 3) {
        // اگر در سه صفحه اول هستیم
        for (let i = 1; i <= 3; i++) {
          pagination.push(
            <button
              key={i}
              onClick={() => handlePageChange(i)}
              className={`mx-2 px-4 py-2 rounded-lg ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
            >
              {i}
            </button>
          );
        }
        pagination.push(<span key="dots" className="mx-2">...</span>);
        pagination.push(
          <button
            key={totalPages}
            onClick={() => handlePageChange(totalPages)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === totalPages ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {totalPages}
          </button>
        );
      } else {
        // اگر در سه صفحه آخر هستیم
        pagination.push(
          <button
            key={1}
            onClick={() => handlePageChange(1)}
            className={`mx-2 px-4 py-2 rounded-lg ${currentPage === 1 ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
          >
            {1}
          </button>
        );
        pagination.push(<span key="dots" className="mx-2">...</span>);
        for (let i = totalPages - 4; i <= totalPages; i++) {
          pagination.push(
            <button
              key={i}
              onClick={() => handlePageChange(i)}
              className={`mx-2 px-4 py-2 rounded-lg ${currentPage === i ? 'bg-blue-500 text-white' : 'bg-gray-300'}`}
            >
              {i}
            </button>
          );
        }
      }
    }

    return pagination;
  };

  const handleLogout = () => {
    clearToken(); // Clear the token
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
        />

        <main className='main flex justify-between w-full py-8 px-2 lg:px-8 '>
          <Aside
            openSelect={openSelect}
            handleSelectToggle={handleSelectToggle}
            options={[options1, options2, options3, options4]} // Pass options as an array
          />
          <section className='grid gap-8 w-5/6 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4'>
            {currentImages.map((image) => (
              <Card key={image.id} image={image} />
            ))}
          </section>
        </main>

        <div className="pagination flex justify-center py-4">
          {renderPagination()}
        </div>

        <Footer />

        <button
          className='fixed right-3 bottom-3 
        w-10 h-10 p-6 flex justify-center items-center
        bg-blue-700 text-white text-2xl outline-none
        rounded-full cursor-pointer'>
          <Link href={'/uploadImage'}>+</Link>
        </button>

        {/* <MouseEffect /> */}

      </div>
    </>
  );
}

export default Index;