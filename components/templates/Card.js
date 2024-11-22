import { useState } from 'react';
import { MdFullscreen } from "react-icons/md";
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import Link from 'next/link';
import FullScreenModal from '@/components/templates/FullScreenModal'; // Import the modal

function Card({ image }) {

    const [isModalOpen, setModalOpen] = useState(false);

    const downloadLink = `/downloader/${image.id}?title=${encodeURIComponent(image.title)}&path=${encodeURIComponent(image.path)}`;

    const handleOpenModal = () => {
        setModalOpen(true); // Open the modal
    };

    const handleCloseModal = () => {
        setModalOpen(false); // Close the modal
    };

    return (
        <div className='card w-full rounded-lg overflow-hidden bg-bgDarkGray'>
            <div className='inside-card w-full px-3 pt-3'>
                <div className='bg-img relative w-full h-52 flex justify-center items-center p-2 rounded-md'>
                    <div className='absolute top-2 right-2 text-white p-1 rounded-md opacity-60 cursor-pointer'
                        onClick={handleOpenModal}>
                        <MdFullscreen className='text-xl' />
                    </div>
                    <img src={`../../img/${image.path}`} alt={image.fileTitle} className='w-full h-full object-contain' />
                </div>
                <div className='info-img w-full py-3'>
                    <h3 className='block text-xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>{image.title}</h3>
                    <div className='flex mt-3'>
                        <div className='flex justify-between items-center bg-gray-600 px-2 py-1 rounded text-xs text-lightBlue'>
                            <FaImage />
                            <span className='block ml-1'>{image.size}</span>
                        </div>
                        <div className='flex justify-between items-center bg-gray-600 ml-2 px-2 py-1 rounded text-xs text-lightBlue'>
                            <RxDimensions />
                            <span className='block ml-1'>{`${image.width} × ${image.height}`}</span>
                        </div>
                    </div>
                    <Link href={downloadLink} class="Download-button w-full rounded mt-3 py-2">
                        <span>Download</span>
                    </Link>
                </div>
            </div>

            {/* Render the FullScreenModal if it is open */}
            {isModalOpen && <FullScreenModal image={image} onClose={handleCloseModal} />}
        </div>
    );
}

export default Card;