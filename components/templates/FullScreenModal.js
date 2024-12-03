import React, { useEffect } from 'react';

const FullScreenModal = ({ image, onClose }) => {
    // Effect to handle body overflow
    useEffect(() => {
        // Disable scrolling on body when modal is open
        document.body.style.overflow = 'hidden';

        // Cleanup function to reset the overflow when modal is closed
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, []);

    return (
        <div
            className="fullScreenModal fixed inset-0 bg-black/[.7] 
            flex justify-center items-center z-50 backdrop-blur-md overflow-hidden"
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