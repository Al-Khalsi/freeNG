import React, { createContext, useContext, useState } from 'react';

const ImageContext = createContext();

export const ImageProvider = ({ children }) => {
    const [imageData, setImageData] = useState(null);

    return (
        <ImageContext.Provider value={{ imageData, setImageData }}>
            {children}
        </ImageContext.Provider>
    );
};

export const useImageContext = () => {
    return useContext(ImageContext);
};