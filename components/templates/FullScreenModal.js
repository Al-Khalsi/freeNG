import React, { useEffect } from 'react';

const FullScreenModal = ({ image, onClose, lightMode }) => {
    useEffect(() => {
        document.body.style.overflow = 'hidden';
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, []);

    return (
        <div
            className={`fullScreenModal fixed inset-0
            flex justify-center items-center z-50 backdrop-blur-md overflow-hidden ${lightMode ? 'bg-white/[0.5]' : 'bg-black/[0.7] '}`}
            onClick={onClose}>
            <button onClick={onClose} className="absolute top-8 right-8 text-2xl">✖</button>
            <div className='relative max-w-full h-full m-auto p-8 z-10 overflow-hidden'>
                <img
                    src={`../../img/${image.path}`}
                    alt={image.fileTitle}
                    className="max-w-full max-h-full object-contain"
                    onClick={(e) => e.stopPropagation()} />
            </div>
        </div>
    );
};

export default FullScreenModal;