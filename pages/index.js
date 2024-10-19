import Link from 'next/link';
import { FaSun, FaMoon, FaSearch } from "react-icons/fa";
import { useEffect, useState } from "react";


function index() {
  const [isDarkMode, setIsDarkMode] = useState(false);

  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark'); // اضافه کردن کلاس dark
    } else {
      document.documentElement.classList.remove('dark'); // حذف کلاس dark
    }
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(prevMode => !prevMode); // تغییر حالت تاریک و روشن
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

  return (
    <>
      <div className="app">
        <header className='header w-full h-24 px-12 flex justify-between items-center'>

          <div className='bg-logo'>
            <div className='logo'>LOGO</div>
          </div>

          <div className='search-box relative'>
            <input type="search" className='search border-none outline-none' placeholder='search...' />
            <button className='btn-search absolute top-0 right-0 flex justify-center items-center'>
              <FaSearch />
            </button>
          </div>

          <div className='right-header flex py-1 px-3'>
            <button className='change-background flex justify-center items-center w-10 h-10 mr-4 rounded-full' onClick={toggleTheme}>
              {isDarkMode ? <FaSun /> : <FaMoon />}
            </button>
            <Link href="/profile" className='w-10 h-10 rounded-full overflow-hidden'>
              <img src="/img/user.png" className='userPng w-full ' alt='profile' title='profile' />
            </Link>
          </div>
        </header>

        <main className='main flex justify-center w-full py-12 sm:justify-between '>
          <aside class="filter-sidebar p-4 rounded-xl w-1/6 hidden sm:block">
            <h2>Filters</h2>
            <ul>
              <li><input type="checkbox" /> فیلتر 1</li>
              <li><input type="checkbox" /> فیلتر 2</li>
              <li><input type="checkbox" /> فیلتر 3</li>
            </ul>
          </aside>
          <section className='grid gap-8 w-5/6 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'>
            {images.map((src, index) => (
              <div key={index} className='card w-full h-64 rounded-xl overflow-hidden'>
                <div className='bg-img w-full h-full bg-gradient-to-r from-gray-600 via-40% to-gray-900 to-68%'>
                  <img src={src} alt={`Image ${index + 1}`} className='w-full h-full object-cover' />
                  <div className='info-img w-ful'></div>
                </div>
              </div>
            ))}
          </section>
        </main>
      </div>
    </>
  )
}

export default index