import Link from 'next/link';
import { FaSun, FaMoon, FaSearch } from "react-icons/fa";
import { useEffect, useState } from "react";


function index() {
  const [isDarkMode, setIsDarkMode] = useState(false);

  useEffect(() => {
    document.body.classList.toggle('dark', isDarkMode);
    document.body.classList.toggle('light', !isDarkMode);
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(prevMode => !prevMode);
  };

  return (
    <>
      <div className="app">
        <header className='header'>

          <div className='bg-logo'>
            <div className='logo'>LOGO</div>
          </div>

          <div className='search-box'>
            <input type="search" className='search' placeholder='search...' />
            <button className='btn-search'>
              <FaSearch />
            </button>
          </div>

          <div className='right-header'>
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