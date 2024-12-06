import React, { useEffect } from 'react';

const FullScreenModal = ({ image, onClose, lightMode }) => {
    useEffect(() => {
        const originalOverflow = document.body.style.overflow;
        document.body.style.overflow = 'hidden';

        const handleKeyDown = (event) => {
            if (event.key === 'Escape') {
                onClose(); 
            }
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            document.body.style.overflow = originalOverflow;
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [onClose]); 

    const backdropClass = lightMode ? 'bg-white/[0.5]' : 'bg-black/[0.7]';

    return (
        <div
            className={`fullScreenModal fixed inset-0 flex justify-center items-center z-50 backdrop-blur-md overflow-hidden ${backdropClass}`}
            onClick={onClose}>
            <button onClick={onClose} className="absolute top-8 right-8 text-2xl">✖</button>
            <div className='relative flex justify-center items-center max-w-full h-full m-auto p-8 z-10 overflow-hidden'>
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