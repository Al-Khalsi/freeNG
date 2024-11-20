import { useRouter } from 'next/router';
import { FaSearch, FaMicrophone } from "react-icons/fa";
import Link from 'next/link';

const downloader = () => {
    const router = useRouter(); 
    const { title, path, } = router.query; 

    return (
        <div className='imageSinglePage w-full min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className='wrapper p-12'>
                <div className='product-div w-full h-80 flex bg-bgDarkGray rounded-3xl m-5 overflow-hidden'>
                    <div className='product-div-left bg-img w-1/3'>
                        <div className="img-container w-full h-full">
                            <img src={`../../img/${path}`} className='w-full h-full object-cover' alt={title} />
                        </div>
                    </div>
                    <div className='product-div-right w-2/3 bg-darkBlue text-white'>
                        <h1 className='image-title'>{title}</h1>
                        
                    </div>
                </div>
            </div>
        </div>
    );
};

export default downloader;