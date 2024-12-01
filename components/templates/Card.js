import React, { useState } from 'react';
import { MdFullscreen, MdDelete, MdEdit } from "react-icons/md";
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import Link from 'next/link';
import FullScreenModal from '@/components/templates/FullScreenModal'; // Import the modal

function Card({ image, role, onDelete, onEdit }) {
    const [isModalOpen, setModalOpen] = useState(false);
    const [editedTitle, setEditedTitle] = useState(image.title);
    const [isEditing, setIsEditing] = useState(false); // Added state for editing mode

    // Update the download link to include additional properties
    const downloadLink = `/downloader/${image.id}?title=${encodeURIComponent(image.title)}&path=${encodeURIComponent(image.path)}&size=${encodeURIComponent(image.size)}&width=${image.width}&height=${image.height}`;

    const handleOpenModal = () => {
        setModalOpen(true); // Open the modal
    };

    const handleCloseModal = () => {
        setModalOpen(false); // Close the modal
    };

    const handleEdit = () => {
        const updatedData = { fileTitle: editedTitle }; // Prepare the updated data
        onEdit(image.id, updatedData); // Call the onEdit prop
        setIsEditing(false); // Close the editing mode
    };

    return (
        <div className={`card w-full rounded-lg overflow-hidden bg-bgDarkGray`}>
            <div className='inside-card w-full px-3 pt-3'>
                <div className={`bg-img relative w-full h-52 flex justify-center items-center rounded-md ${image.lightMode ? 'lightMod' : ''}`}>
                    <div className='absolute top-2 right-2 left-2 flex justify-end text-white text-xl rounded-md opacity-60'>
                        {
                            role === 'ROLE_MASTER' && (
                                <div className='flex'>
                                    <button className='delet flex justify-center items-center w-7 h-7 ml-1 hover:bg-gray-600 rounded-full cursor-pointer'
                                        onClick={() => onDelete(image.id)}>
                                        <MdDelete />
                                    </button>
                                    <button className='edit flex justify-center items-center w-7 h-7 ml-1 hover:bg-gray-600 rounded-full cursor-pointer'
                                        onClick={() => setIsEditing(true)}>
                                        <MdEdit />
                                    </button>
                                </div>
                            )
                        }
                        <button className='fullScreen flex justify-center items-center w-7 h-7 ml-1 hover:bg-gray-600 rounded-full cursor-pointer'
                            onClick={handleOpenModal}>
                            <MdFullscreen />
                        </button>
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
                    <Link href={downloadLink} className="Download-button w-full rounded mt-3 py-2 bg-gray-600 text-clWhite">
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