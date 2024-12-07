import React, { useEffect } from 'react';
import { FaTimes } from 'react-icons/fa';

const KeywordsModal = ({ keywords, onClose }) => {
    useEffect(() => {
        // Function to handle keydown events
        const handleKeyDown = (event) => {
            if (event.key === 'Escape') {
                onClose();
            }
        };

        // Add event listener for keydown
        window.addEventListener('keydown', handleKeyDown);

        // Cleanup function to remove the event listener
        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [onClose]); // Dependency array ensures effect runs when onClose changes

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75 z-50">
            <div className="bg-white rounded-lg p-6 w-11/12 md:w-1/2">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Keywords</h2>
                    <button onClick={onClose} className="text-xl">
                        <FaTimes />
                    </button>
                </div>
                <div className="flex flex-wrap">
                    {keywords.map((kw) => (
                        <span key={kw.id} className="bg-gray-200 rounded px-2 py-1 m-1">
                            {kw.keyword}
                        </span>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default KeywordsModal;