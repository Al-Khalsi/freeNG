import React from 'react';
import { FaInstagram, FaYoutube, FaEnvelope } from 'react-icons/fa'; // Importing icons

function Footer() {
    return (
        <footer className='footer w-full bg-gray-800 text-white py-6'>
            <div className='footer-content w-full flex flex-col md:flex-row justify-between'>
                {/* Left Section */}
                <div className='left w-full md:w-1/3 flex flex-col items-start px-4'>
                    <div className='bg-logo mb-2'>
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
                        <div className='logo text-2xl font-bold'>
                            I<span className='burnt'>m</span>Alchem<span>y</span>
                        </div>
                    </div>
                    <div className='logo-description text-sm'>
                        Lorem ipsum dolor sit amet consectetur adipisicing elit. Explicabo porro, perspiciatis quos odio nulla laborum, harum ex sequi asperiores quidem incidunt illum cupiditate itaque? Repellendus, fugit assumenda. Quidem, eaque iste.
                    </div>
                </div>

                {/* Center Section */}
                <div className='center w-full md:w-1/3 flex flex-col items-center px-4'>
                    <h4 className='text-lg font-semibold mb-2'>Links</h4>
                    <ul className='space-y-1'>
                        <li><a href="#" className='hover:underline'>Upcoming Updates</a></li>
                        <li><a href="#" className='hover:underline'>Developers</a></li>
                        <li><a href="#" className='hover:underline'>Profile Page</a></li>
                    </ul>
                </div>

                {/* Right Section */}
                <div className='right w-full md:w-1/3 flex flex-col items-end px-4'>
                    <h4 className='text-lg font-semibold mb-2'>Follow Us</h4>
                    <div className='flex space-x-4'>
                        <a href="#" aria-label="Gmail" className='text-white hover:text-gray-400'><FaEnvelope size={24} /></a>
                        <a href="#" aria-label="YouTube" className='text-white hover:text-gray-400'><FaYoutube size={24} /></a>
                        <a href="#" aria-label="Instagram" className='text-white hover:text-gray-400'><FaInstagram size={24} /></a>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;