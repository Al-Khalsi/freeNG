import { useRouter } from 'next/router';
import { FaSearch, FaMicrophone } from "react-icons/fa";
import Link from 'next/link';

const Product = () => {
    const router = useRouter();

    const { id } = router.query;

    return (
        <div className='imageSinglePage w-full min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <header className='header w-full h-24 overflow-hidden px-2 md:px-12 flex justify-between items-center bg-darkBlue text-white'>
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

                <div className='right-header flex py-1'>
                    <button className='sm:hidden flex justify-center items-center w-10 h-10 mr-4 rounded-full text-xl bg-black text-white'>
                        <FaSearch />
                    </button>
                    <Link href="/validation" className='w-10 h-10 rounded-full overflow-hidden'>
                        <img src="/img/user.png" className='userPng w-full ' alt='profile' title='profile' />
                    </Link>
                </div>
            </header>

            <div className='wrapper'>
                <div className='product-div bg-white'>
                    <div className='product-div-left'>
                        <div className="img-container">
                            <img src="" alt="" />
                        </div>
                    </div>
                    <div className='product-div-right'>
                        <h1 className='image-title'>Image Title Testing</h1>
                        
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Product;