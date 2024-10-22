// pages/index.js
import Link from 'next/link';
import { FaSun, FaMoon, FaSearch, FaMicrophone } from "react-icons/fa";
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
        <header className='header w-full h-24 overflow-hidden px-2 md:px-12 flex justify-between items-center bg-darkBlue'>
          <div className='bg-logo'>
            <div className='logo'>LOGO</div>
          </div>

          <div id='search' className='search-box z-0 w-2/5 hidden sm:block'>
            <div id="search-container" className="flex justify-center items-center">
              <div className="nebula w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm"></div>
              <div className="starfield w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm"></div>
              <div className="cosmic-dust"></div>
              <div className="cosmic-dust"></div>
              <div className="cosmic-dust"></div>

              <div className="stardust w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm max-h-16"></div>

              <div className="cosmic-ring w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm"></div>

              <div id="main">
                <input
                  className="input border-none rounded-xl text-lg"
                  name="text"
                  type="text"
                  placeholder="Search..."
                />
                <div id="input-mask"></div>
                <div id="cosmic-glow"></div>
                <div className="wormhole-border"></div>
                <div id="wormhole-icon">
                  <FaMicrophone className='text-blue-300' />
                </div>
                <div id="search-icon">
                  <svg
                    stroke-linejoin="round"
                    stroke-linecap="round"
                    stroke-width="2"
                    stroke="url(#cosmic-search)"
                    fill="none"
                    height="24"
                    width="24"
                    viewBox="0 0 24 24"
                  >
                    <circle r="8" cy="11" cx="11"></circle>
                    <line y2="16.65" x2="16.65" y1="21" x1="21"></line>
                    <defs>
                      <linearGradient gradientTransform="rotate(45)" id="cosmic-search">
                        <stop stop-color="#a9c7ff" offset="0%"></stop>
                        <stop stop-color="#6e8cff" offset="100%"></stop>
                      </linearGradient>
                    </defs>
                  </svg>
                </div>
              </div>
            </div>

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
              <div key={index} className='card w-full h-96 rounded-2xl overflow-hidden bg-darkBlue'>
                <div className='inside-card h-full w-full p-3'>
                <div className='bg-img w-full h-2/3 flex justify-center items-center p-2 rounded-xl'>
                  <img src={src} alt={`Image ${index + 1}`} className='w-full h-full object-continer' />
                </div>
                <div className='info-img w-full h-1/3 px-2 py-3'>
                  <h3 className='block text-xl text-white'>title</h3>
                  <div className='flex justify-between mt-3'>
                    <div className='flex flex-col w-1/3 text-center p-2 text-lightBlue'>
                      <span className='block text-sm'>800KB</span>
                      <span className='text-xs'>Size</span>
                    </div>
                    <div className='flex flex-col w-1/3 text-center p-2 text-lightBlue border-x-2 border-lightGray'>
                      <span className='block text-sm'>1080*720</span>
                      <span className='text-xs'>Resolution</span>
                    </div>
                    <div className='flex flex-col w-1/3 text-center p-2 text-lightBlue'>
                      <span className='block text-sm'>1200</span>
                      <span className='text-xs'>Download</span>
                    </div>
                  </div>
                </div>
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