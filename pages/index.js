// pages/index.js
import Link from 'next/link';
import { FaSun, FaMoon, FaSearch } from "react-icons/fa";
import { useEffect, useState } from "react";
import Head from 'next/head';
import SelectWithSearch from "../components/modules/SelectWithSearch";

function Index() {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [openSelect, setOpenSelect] = useState(null); // وضعیت برای شناسایی سلکتور باز

  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(prevMode => !prevMode);
  };

  const images = [
    "/img/fire.png",
    "/img/h.png",
    "/img/hh.png",
    "/img/e.png",
    "/img/fire.png",
    "/img/h.png",
    "/img/hh.png",
    "/img/e.png"
  ];

  // تعریف گزینه‌های مختلف برای هر SelectWithSearch
  const options1 = ['html', 'css', 'js', 'react', 'next'];
  const options2 = ['node', 'express', 'mongodb', 'graphql'];
  const options3 = ['python', 'django', 'flask', 'fastapi'];

  const handleSelectToggle = (selectId) => {
    // اگر سلکتور فعلی باز است، آن را ببندید
    if (openSelect === selectId) {
      setOpenSelect(null);
    } else {
      setOpenSelect(selectId); // باز کردن سلکتور جدید
    }
  };

  return (
    <>
      <div className="app">
        <header className='header w-full h-24 px-2 md:px-12 flex justify-between items-center'>
          <div className='bg-logo'>
            <div className='logo'>LOGO</div>
          </div>

          <div className='search-box relative hidden sm:block'>
            <input type="search" className='search border-none outline-none' placeholder='search...' />
            <button className='btn-search absolute top-0 right-0 flex justify-center items-center'>
              <FaSearch />
            </button>
          </div>

          <div className='right-header flex py-1'>
            <button className='sm:hidden flex justify-center items-center w-10 h-10 mr-4 rounded-full text-xl bg-black text-white'>
              <FaSearch />
            </button>
            <button className='change-background flex justify-center items-center w-10 h-10 mr-4 rounded-full' onClick={toggleTheme}>
              {isDarkMode ? <FaSun /> : <FaMoon />}
            </button>
            <Link href="/profile" className='w-10 h-10 rounded-full overflow-hidden'>
              <img src="/img/user.png" className='userPng w-full ' alt='profile' title='profile' />
            </Link>
          </div>
        </header>

        <aside className="filter-sidebar w-full">
          <div className="flex px-12 py-2 bg-gray-500">
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
          <section className='grid gap-16 w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4'>
            {images.map((src, index) => (
              <div key={index} className='card w-full rounded-xl overflow-hidden'>
                <div className='bg-img w-full h-full flex justify-center items-center p-2 bg-gradient-to-r from-gray-600 via-40% to-gray-900 to-68%'>
                  <img src={src} alt={`Image ${index + 1}`} className='w-full object-cover' />
                  <div className='info-img w-ful'></div>
                </div>
              </div>
            ))}
          </section>
        </main>
      </div>
    </>
  );
}

export default Index;