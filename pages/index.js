
import ParticlesComponent from '@/components/templates/background/particles';
import Link from 'next/link';
import { FaSun, FaMoon } from "react-icons/fa";

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
          </div>

          <div className='right-header'>
            <button className='change-background'>
              <FaMoon />
            </button>
            <Link href="/profile">
              profile
            </Link>
          </div>
        </header>
        <h1>home page</h1>
        <Link href="/product/1">Go to product page 1</Link>
        <br />
        <Link href="/product/2">Go to product page 2</Link>
      </div>
    </>
  )
}

export default index