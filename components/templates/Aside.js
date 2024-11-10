import React, { useState } from 'react';
import { BiSolidCategory } from "react-icons/bi";
import { PiTreeViewFill } from "react-icons/pi";
import { IoIosArrowDown, IoMdColorPalette } from "react-icons/io";
import { FaShapes } from "react-icons/fa";

const Aside = () => {
    // Using an object to track the open state of each menu
    const [openMenus, setOpenMenus] = useState({});

    const toggleMenu = (menuIndex) => {
        setOpenMenus((prev) => ({
            ...prev,
            [menuIndex]: !prev[menuIndex] // Toggle the specific menu
        }));
    };

    return (
        <aside className='filter-sidebar flex flex-col w-1/6 mr-8'>
            <div className='filtering bg-bgDarkGray2 rounded-xl px-2 py-2'>
                <ul>
                    <li>
                        <button 
                            className="dropdown-btn w-full flex justify-between items-center gap-1 rounded-lg p-3 no-underline"
                            onClick={() => toggleMenu(1)}
                        >
                            <div className="flex items-center gap-3">
                                <BiSolidCategory />
                                <span className="grow">Category</span>
                            </div>
                            <IoIosArrowDown className="shrink-0" />
                        </button>
                        <ul className={`sub-menu transition-all duration-300 ease-in-out ${openMenus[1] ? 'max-h-40 opacity-100' : 'max-h-0 opacity-0 overflow-hidden'}`}>
                            <li>test 1</li>
                            <li>test 2</li>
                            <li>test 3</li>
                            <li>test 4</li>
                            <li>test 5</li>
                        </ul>
                    </li>
                    <li>
                        <button 
                            className="dropdown-btn w-full flex justify-between items-center gap-1 rounded-lg p-3 no-underline"
                            onClick={() => toggleMenu(2)}
                        >
                            <div className="flex items-center gap-3">
                                <PiTreeViewFill />
                                <span>Sub Category</span>
                            </div>
                            <IoIosArrowDown />
                        </button>
                        <ul className={`sub-menu transition-all duration-300 ease-in-out ${openMenus[2] ? 'max-h-40 opacity-100' : 'max-h-0 opacity-0 overflow-hidden'}`}>
                            <li>test 1</li>
                            <li>test 2</li>
                            <li>test 3</li>
                            <li>test 4</li>
                            <li>test 5</li>
                        </ul>
                    </li>
                    <li>
                        <button 
                            className="dropdown-btn w-full flex justify-between items-center gap-1 rounded-lg p-3 no-underline"
                            onClick={() => toggleMenu(3)}
                        >
                            <div className="flex items-center gap-3">
                                <FaShapes />
                                <span>Style PNG</span>
                            </div>
                            <IoIosArrowDown />
                        </button>
                        <ul className={`sub-menu transition-all duration-300 ease-in-out ${openMenus[3] ? 'max-h-40 opacity-100' : 'max-h-0 opacity-0 overflow-hidden'}`}>
                            <li>test 1</li>
                            <li>test 2</li>
                            <li>test 3</li>
                            <li>test 4</li>
                            <li>test 5</li>
                        </ul>
                    </li>
                    <li>
                        <button 
                            className="dropdown-btn w-full flex justify-between items-center gap-1 rounded-lg p-3 no-underline"
                            onClick={() => toggleMenu(4)}
                        >
                            <div className="flex items-center gap-3">
                                <IoMdColorPalette className="text-lg" />
                                <span>Color</span>
                            </div>
                            <IoIosArrowDown />
                        </button>
                        <ul className={`sub-menu transition-all duration-300 ease-in-out ${openMenus[4] ? 'max-h-40 opacity-100' : 'max-h-0 opacity-0 overflow-hidden'}`}>
                            <li>test 1</li>
                            <li>test 2</li>
                            <li>test 3</li>
                            <li>test 4</li>
                            <li>test 5</li>
                        </ul>
                    </li>
                </ul>
            </div>
        </aside>
    );
};

export default Aside;