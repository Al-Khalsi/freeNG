// pages/index.js
import Link from 'next/link';
import { useAuth } from '../context/AuthContext';
import { useEffect, useState } from "react";
import { MdFullscreen } from "react-icons/md";
import SelectWithSearch from "../components/modules/SelectWithSearch";
import Images from "../data/db.json";
import { useRouter } from 'next/router';
import Header from '@/components/templates/header/Header';

function Index() {
  const { token, username, email, clearToken, userId } = useAuth(); // Destructure token and username from Auth context
  const router = useRouter();
  const [openSelect, setOpenSelect] = useState(null);
  const itemsPerPage = 20;
  const currentPage = parseInt(router.query.page) || 1;

  const options1 = ['html', 'css', 'js', 'react', 'next'];
  const options2 = ['node', 'express', 'mongodb', 'graphql'];

  const handleSelectToggle = (selectId) => {
    if (openSelect === selectId) {
      setOpenSelect(null);
    } else {
      setOpenSelect(selectId);
    }
  };

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
    router.push('/validation'); // Redirect to the login page
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

        <aside className="filter-sidebar w-full">
          <div className="flex px-12 py-2 bg-gray-0">
            <SelectWithSearch
              options={options1}
              defaultText="Category"
              isOpen={openSelect === 1}
              onToggle={() => handleSelectToggle(1)}
            />
            <SelectWithSearch
              options={options2}
              defaultText="Style"
              isOpen={openSelect === 2}
              onToggle={() => handleSelectToggle(2)}
            />
          </div>
        </aside>

        <main className='main flex justify-center w-full py-8 px-2 lg:px-12'>
          <section className='grid gap-10 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'>
            {currentImages.map((image) => (
              // <Link href={`/product/${image.id}
              // ?title=${encodeURIComponent(image.Title)}
              // &size=${encodeURIComponent(image.Size)}
              // &dimensions=${encodeURIComponent(image.Dimensions)}
              // &download=${encodeURIComponent(image.Download)}
              // &src=${encodeURIComponent(image.Src)}`} key={image.id}>
                <div className='card w-full h-80 rounded-2xl overflow-hidden bg-darkBlue' key={image.id}>
                  <div className='inside-card h-full w-full p-3'>
                    <div className='bg-img relative w-full h-2/3 flex justify-center items-center p-2 rounded-xl'>
                      <div className='absolute top-2 right-2 bg-darkBlue text-white p-1 rounded-md opacity-60'>
                        <MdFullscreen className='text-xl' />
                      </div>
                      <img src={image.Src} alt={image.Title} className='w-full h-full object-continer' />
                    </div>
                    <div className='info-img w-full h-1/3 px-2 py-3'>
                      <h3 className='block text-xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>{image.Title}</h3>
                      <div className='flex justify-between mt-3'>
                        <div className='flex flex-col w-1/3 text-center pr-2 text-lightBlue'>
                          <span className='block text-sm'>{image.Size}</span>
                          <span className='text-xs'>Size</span>
                        </div>
                        <div className='flex flex-col w-1/3 text-center text-lightBlue border-x-2 border-lightGray'>
                          <span className='block text-sm'>{image.Dimensions}</span>
                          <span className='text-xs'>Dimensions</span>
                        </div>
                        <div className='flex flex-col w-1/3 text-center pl-2 text-lightBlue'>
                          <span className='block text-sm'>{image.Download}</span>
                          <span className='text-xs'>Download</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              // </Link>
            ))}
          </section>
        </main>

        <div className="pagination flex justify-center py-4">
          {renderPagination()}
        </div>

        <button 
        className='fixed left-2 bottom-2 
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