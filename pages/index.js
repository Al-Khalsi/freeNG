import Link from 'next/link';

function index() {
  return (
    <div className="main">
      <header className='header'>

        <div className='bg-logo'>
          <div className='logo'></div>
        </div>

        <div className='search-box'>
          <input type="search" />
        </div>

        <div className='right-header'>
          <button></button>
          <Link href="/profile">profile</Link>
        </div>
      </header>
      <h1>home page</h1>
      <Link href="/product/1">Go to product page 1</Link>
      <br />
      <Link href="/product/2">Go to product page 2</Link>
    </div>
  )
}

export default index