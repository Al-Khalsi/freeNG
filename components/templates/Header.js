import { useEffect, useRef, useState } from 'react';
import Link from 'next/link';
import { FaSearch } from 'react-icons/fa';
import { IoLogInSharp, IoLogOut } from "react-icons/io5";
import { useAuth } from "@/context/AuthContext"
import Logo from './Logo';

function Header({ searchQuery, setSearchQuery, handleSearch }) {
    const { token, username, email, clearToken, userId, role } = useAuth();
    const [isFixedHeader, setIsFixedHeader] = useState(false);
    const [isSearchBarVisible, setIsSearchBarVisible] = useState(false);
    const searchBarRef = useRef(null);
    const searchInputRef = useRef(null);

    // Effect to handle scroll event to make header sticky
    useEffect(() => {
        const handleScroll = () => {
            if (window.scrollY > 35) {
                setIsFixedHeader(true);
            } else {
                setIsFixedHeader(false);
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    const toggleSearchBar = () => {
        setIsSearchBarVisible(!isSearchBarVisible);
    };

    // Effect to close the search bar when window resizes above 768px
    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth > 768) {
                setIsSearchBarVisible(false); // Hide search bar on wider screens
            }
        };

        window.addEventListener('resize', handleResize);
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

    // Effect to handle clicks outside the search bar
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (searchBarRef.current && !searchBarRef.current.contains(event.target)) {
                setIsSearchBarVisible(false); // Close the search bar if clicked outside
            }
        };

        // Bind the event listener
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    // Effect to focus the search input when the search bar is visible
    useEffect(() => {
        if (isSearchBarVisible && searchInputRef.current) {
            searchInputRef.current.focus(); // Focus the search input when the search bar is visible
        }
    }, [isSearchBarVisible]);

    const handleLogout = () => {
        clearToken();
    };

    return (
        <header className={`header sticky top-0 w-full h-24 px-4 md:px-8 
        flex justify-between items-center text-white bg-bgDarkBlue z-50 ${isFixedHeader ? 
        'shadow-custom-inset shadow-bgPurple' : ''}`}>
            
            <Logo />

            <div id='search' className='search-box z-0 w-2/5 hidden md:block'>
                <div id="search-container" className="flex justify-center items-center ">
                    <div className="nebula w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm"></div>
                    <div className="cosmic-ring w-full h-full absolute overflow-hidden -z-10 rounded-xl blur-sm"></div>

                    <div id="main">
                        <input
                            className="input border-none rounded-xl text-lg"
                            name="text"
                            type="text"
                            placeholder="Search..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === 'Enter') {
                                    e.preventDefault();
                                    if (searchQuery.trim()) {
                                        handleSearch();
                                    }
                                }
                            }}
                            autoComplete="off"
                        />
                        <div id="wormhole-icon">
                            <button
                                type='button'
                                className={`text-white ${!searchQuery.trim() ? 'cursor-not-allowed' : ''}`}
                                onClick={handleSearch}
                                disabled={!searchQuery.trim()}
                            >
                                Enter
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div className='flex'>
                {/* Mobile Search Button */}
                <button
                    className='md:hidden flex justify-center items-center w-12 h-12 mr-2 rounded-md text-xl text-white bg-gradient-to-t from-bgLightPurple to-bgPurple'
                    onClick={toggleSearchBar}
                    aria-label="Search">
                    <FaSearch />
                </button>

                {token ? (
                    <div className='flex items-center border-2 border-bgLightPurple rounded-md py-1 px-2'>
                        <div>
                            <h2 className='text-white cursor-pointer' title={username}>
                                {username.length > 8 ? `${username.slice(0, 8)}...` : username}
                            </h2>
                        </div>
                        <IoLogOut onClick={handleLogout} className='ml-4 text-red-600 cursor-pointer' />
                    </div>
                ) : (
                    <div className='flex'>
                        <Link href="/authentication"
                            className="button rounded-md"
                            title="Log in or register an account"
                            aria-label="Log in or register an account">
                            <IoLogInSharp className='sm:hidden text-2xl' />
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
                            </div>
                            <span className="inner">
                                Log In / Sign Up
                            </span>
                        </Link>
                    </div>
                )}
            </div>

            {/* Mobile Search Bar */}
            {
                isSearchBarVisible && (
                    <div ref={searchBarRef} className="absolute top-0 left-0 w-full border-b border-bgLightPurple bg-bgDarkBlue p-2 z-50">
                        <div className="relative flex justify-between items-center">
                            <input
                                ref={searchInputRef}
                                className="input border-none rounded-xl text-lg w-full"
                                name="text"
                                type="text"
                                placeholder="Search..."
                                value={searchQuery} // Keep the search query in the input
                                onChange={(e) => setSearchQuery(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        e.preventDefault();
                                        if (searchQuery.trim()) {
                                            handleSearch(); // Call the search function on Enter
                                        }
                                    }
                                }}
                                autoComplete="off"
                            />
                        </div>
                        <button
                            type='button'
                            className={`absolute right-5 top-1/2 -translate-y-1/2 text-white bg-gradient-to-t from-bgLightPurple to-bgPurple
                        rounded p-3 ${!searchQuery.trim() ? 'cursor-not-allowed' : ''}`}
                            onClick={handleSearch}
                            disabled={!searchQuery.trim()}
                        >
                            Search
                        </button>
                    </div>
                )
            }
        </header>
    );
}

export default Header;