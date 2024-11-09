import { BiSolidCategory } from "react-icons/bi";
import { PiTreeViewFill, PiTreeStructureFill } from "react-icons/pi";
import { IoIosArrowDown } from "react-icons/io";

const Aside = () => {
    return (
        <aside className='filter-sidebar flex flex-col w-1/6 mr-8'>
            <div className='filtering bg-bgDarkGray2 rounded-xl'>
               <ul>
                <li>
                    <button className="dropdown-btn flex items-center gap-4 rounded-lg p-3 no-underline">
                        <BiSolidCategory />
                        <span className="grow">Category</span>
                        <IoIosArrowDown className="shrink-0"/>
                    </button>
                    <ul className="sub-menu">
                        <li>test 1</li>
                        <li>test 2</li>
                        <li>test 3</li>
                        <li>test 4</li>
                        <li>test 5</li>
                    </ul>
                </li>
                <li>
                    <button className="dropdown-btn">
                        <PiTreeViewFill />
                        <span>Sub Category</span>
                        <IoIosArrowDown />
                    </button>
                    <ul className="sub-menu">
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