import React, { useEffect } from 'react';
import { FaTimes } from 'react-icons/fa';

const KeywordsModal = ({ keywords, onClose, onKeywordClick }) => {
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

    // Function to handle clicks inside the modal content
    const handleContentClick = (event) => {
        event.stopPropagation(); // Prevent click from bubbling up to the overlay
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75 z-50" onClick={onClose}>
            <div 
                className="bg-bgDarkGray rounded p-6 w-11/12 md:w-1/2" 
                onClick={handleContentClick} // Add click handler here
            >
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Tag</h2>
                    <button onClick={onClose} className="text-xl">
                        <FaTimes />
                    </button>
                </div>
                <div className="flex flex-wrap">
                    {keywords.map((kw) => (
                        <button 
                            key={kw.id} 
                            onClick={() => onKeywordClick(kw.id)} // Call the keyword click handler
                            className="bg-bgDarkGray2 rounded px-2 py-1 m-1 text-white"
                        >
                            {kw.keyword}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default KeywordsModal;