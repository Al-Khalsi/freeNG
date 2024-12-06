import React, { useEffect, useRef } from 'react';

const MouseEffect = () => {
    const cursorRef = useRef(null);

    useEffect(() => {
        const cursor = cursorRef.current;

        const handleMouseMove = (e) => {
            cursor.style.top = e.pageY + 'px';
            cursor.style.left = e.pageX + 'px';
        };

        window.addEventListener('mousemove', handleMouseMove);

        // Cleanup function to remove the event listener
        return () => {
            window.removeEventListener('mousemove', handleMouseMove);
        };
    }, []);

    return (
        <div
            id='pixelCursor'
            ref={cursorRef}
        />
    );
};

export default MouseEffect;