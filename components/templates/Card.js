import React, { useState } from 'react';
import { MdFullscreen, MdDelete, MdEdit } from "react-icons/md";
import { FaImage } from "react-icons/fa";
import { RxDimensions } from "react-icons/rx";
import Link from 'next/link';
import FullScreenModal from '@/components/templates/FullScreenModal';

function Card({ image, role, onDelete, onEdit }) {
    const [isModalOpen, setModalOpen] = useState(false);
    const [editedTitle, setEditedTitle] = useState(image.title);
    const [isEditing, setIsEditing] = useState(false);

    const downloadLink = `/download/${image.id}?${new URLSearchParams({
        title: image.title,
        size: image.size,
        width: image.width,
        height: image.height,
        style: image.style,
        dominantColors: image.dominantColors,
        keywords: image.keywords,
        lightMode: image.lightMode ? 'true' : 'false',
        path: image.path,
        source: image.source
    }).toString()}`;

    const handleOpenModal = () => {
        setModalOpen(true);
    };

    const handleCloseModal = () => {
        setModalOpen(false);
    };

    const handleEdit = () => {
        const updatedData = { fileTitle: editedTitle };
        onEdit(image.id, updatedData);
        setIsEditing(false);
    };

    return (
        <div className={`card w-full rounded-lg overflow-hidden bg-bgDarkGray`}>
            <div className='inside-card w-full px-3 pt-3'>
                <div className={`bg-img relative w-full h-52 p-4 flex justify-center items-center rounded-md ${image.lightMode ? 'lightMod' : ''}`}>
                    <div className='absolute top-2 right-2 left-2 flex justify-end text-white text-xl rounded-md'>
                        {role === 'ROLE_MASTER' && (
                            <div className='flex'>
                                <button className={`delet flex justify-center items-center 
                                w-7 h-7 ml-1 hover:bg-gray-600 rounded-md cursor-pointer 
                                ${image.lightMode ? 'text-clDarkBlue hover:text-clGray hover:bg-bgDarkBlue' : ''}`}
                                    onClick={() => onDelete(image.id)}>
                                    <MdDelete />
                                </button>
                                <button className={`edit flex justify-center items-center 
                                w-7 h-7 ml-1 hover:bg-gray-600 rounded-md cursor-pointer 
                                ${image.lightMode ? 'text-clDarkBlue hover:text-clGray hover:bg-bgDarkBlue' : ''}`}
                                    onClick={() => setIsEditing(true)}>
                                    <MdEdit />
                                </button>
                            </div>
                        )}
                        <button className={`fullScreen flex justify-center items-center 
                        w-7 h-7 ml-1 hover:bg-gray-600 rounded-md cursor-pointer 
                        ${image.lightMode ? 'text-clDarkBlue hover:text-clGray hover:bg-bgDarkBlue' : ''}`}
                            onClick={handleOpenModal}
                            aria-label="Fullscreen">
                            <MdFullscreen />
                        </button>
                    </div>
                    <img src={`${image.path}`} alt={image.title} className='w-full h-full object-contain' />
                </div>
                <div className='info-img w-full py-3'>
                    <h2 className='block text-xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>{image.title}</h2>
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
                    <Link href={downloadLink} className="Download-button relative flex justify-center items-center 
                    w-full mt-3 py-3 px-5 text-lg font-medium rounded-lg border-none bg-gray-600 text-clWhite 
                    cursor-pointer duration-200">
                        <span>Download</span>
                    </Link>
                </div>
            </div>

            {/* Pass lightMode to FullScreenModal */}
            {isModalOpen && <FullScreenModal image={image} onClose={handleCloseModal} lightMode={image.lightMode} />}
        </div>
    );
}

export default Card;