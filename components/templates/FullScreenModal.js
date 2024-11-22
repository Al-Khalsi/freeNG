import React from 'react';

const FullScreenModal = ({ image, onClose }) => {
    return (
        <div className="fullScreenModal fixed inset-0 bg-black/[.7] flex justify-center items-center z-50">
            <button onClick={onClose} className="absolute top-4 right-4 text-white text-2xl">✖</button>
            <img src={`../../img/${image.path}`} alt={image.fileTitle} className="max-w-full max-h-full object-contain" />
        </div>
    );
};

export default FullScreenModal;