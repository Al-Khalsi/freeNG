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

  return (
    <>
      <div className="app">
        <header className='header w-full h-24 px-12 flex justify-between items-center'>

          <div className='bg-logo'>
            <div className='logo'>LOGO</div>
          </div>

          <div className='search-box'>
            <input type="search" className='search' placeholder='search...' />
            <button className='btn-search'>
              <FaSearch />
            </button>
          </div>

          <div className='right-header flex py-1 px-3'>
            <button className='change-background' onClick={toggleTheme}>
              {isDarkMode ? <FaSun /> : <FaMoon />}
            </button>
            <Link href="/profile">
              <img src="/img/user.png" className='userPng' alt='profile' title='profile' />
            </Link>
          </div>
        </header>

        <div className='main'>
          <aside class="filter-sidebar">
            <h2>Filters</h2>
            <ul>
              <li><input type="checkbox" /> فیلتر 1</li>
              <li><input type="checkbox" /> فیلتر 2</li>
              <li><input type="checkbox" /> فیلتر 3</li>
            </ul>
          </aside>
          <section>
            <h1>home page</h1>
            <Link href="/product/1">Go to product page 1</Link>
            <br />
            <div className='card'>
              <div className='bg-img'>
                <img src="/img/fire.png" alt="" />
              </div>
              <div className='info-img'>
                <h3>fire</h3>
                <b>category / style </b>
              </div>
            </div>
          </section>
        </div>
      </div>
    </>
  )
}

export default index