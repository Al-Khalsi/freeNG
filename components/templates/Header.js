import Link from 'next/link';
import { FaSearch, FaMicrophone } from 'react-icons/fa';

function Header({ token, username, email, userId, handleLogout }) {
    return (
        <header className='header w-full h-24 px-2 md:px-8 flex justify-between items-center text-white'>
            <div className='bg-logo'>
                <div className="points_wrapper">
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                    <i className="point"></i>
                </div>
                <div className='logo'>
                    I<span className='burnt'>m</span>Alchem<span>y</span>
                </div>
            </div>

            <div id='search' className='search-box z-0 w-2/5 hidden sm:block'>
                <div id="search-container" className="flex justify-center items-center">
                    {/* محتوای جعبه جستجو */}
                </div>
            </div>

            <button className='sm:hidden flex justify-center items-center w-10 h-10 mr-4 rounded-full text-xl bg-black text-white'>
                <FaSearch />
            </button>

            {token ? ( // بررسی وجود توکن
                <div>
                    <Link href={`/profile/${userId}?email=${encodeURIComponent(email.trim())}`}>
                        <span className='text-white'>{email}</span>
                    </Link>
                    <button onClick={handleLogout} className='ml-4 px-4 py-2 rounded-lg bg-red-500 text-white'>
                        Logout
                    </button>
                </div>
            ) : (
                <button type="button" className="button rounded-xl">
                    <span className="fold"></span>
                    <div className="points_wrapper">
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                        <i className="point"></i>
                    </div>
                    <span className="inner">
                        <Link href="/validation" className=''>
                            Login / Register
                        </Link>
                    </span>
                </button>
            )}
        </header>
    );
}

export default Header;