import ParticlesComponent from '@/components/templates/background/particles';
import Link from 'next/link';
import { FaSun, FaMoon, FaSearch } from "react-icons/fa";


function index() {

  return (
    <>
      <ParticlesComponent id='particles' />
      <div className="main">
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
            <button className='change-background'>
              <FaMoon />
            </button>
            <Link href="/profile">
              <img src="/img/user.png" className='userPng' alt='profile' title='profile' />
            </Link>
          </div>
        </header>
        <h1>home page</h1>
        <Link href="/product/1">Go to product page 1</Link>
        <br />
        <Link href="/product/2">Go to product page 2</Link>
        <br />
      </div>
    </>
  )
}

export default index