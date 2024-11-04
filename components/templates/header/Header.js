import Link from 'next/link';
import { FaSearch, FaMicrophone } from 'react-icons/fa';

function Header({ token, username, email, userId, handleLogout }) {
    return (
        <header className='header w-full h-24 px-2 md:px-8 flex justify-between items-center text-white'>

            <div className='bg-logo'>
                <div class="points_wrapper">
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                    <i class="point"></i>
                </div>
                <div className='logo'>
                    I<span className='burnt'>m</span>Alchem<span>y</span>
                </div>
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
                        <div id="cosmic-glow"></div>
                        <div className="wormhole-border"></div>
                        <div id="wormhole-icon">
                            <FaMicrophone className='text-blue-300' />
                        </div>
                        <div id="search-icon">
                            <svg
                                strokeLinejoin="round"
                                strokeLinecap="round"
                                strokeWidth="2"
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
                                        <stop stopColor="#a9c7ff" offset="0%"></stop>
                                        <stop stopColor="#6e8cff" offset="100%"></stop>
                                    </linearGradient>
                                </defs>
                            </svg>
                        </div>
                    </div>
                </div>

            </div>

            <button className='sm:hidden flex justify-center items-center w-10 h-10 mr-4 rounded-full text-xl bg-black text-white'>
                <FaSearch />
            </button>
            {token ? ( // Check if token exists
                <div>
                    <Link href={`/profile/${userId}?username=${encodeURIComponent(username.trim())}&email=${encodeURIComponent(email.trim())}`}>
                        <span className='text-white'>{username}</span>
                    </Link>
                    <button onClick={handleLogout} className='ml-4 px-4 py-2 rounded-lg bg-red-500 text-white'>
                        Logout
                    </button>
                </div>
            ) : (
                <button type="button" class="button rounded-xl">
                    <span class="fold"></span>

                    <div class="points_wrapper">
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                        <i class="point"></i>
                    </div>

                    <span class="inner">
                        <Link href="/validation" className=''>
                            Login / Register
                        </Link>
                    </span>
                </button>

            )}
        </header>
    )
}

export default Header