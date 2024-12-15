import React from 'react'
import { MdImageNotSupported } from "react-icons/md";

function NoImages() {
    return (
        <>
            <section className='flex flex-col items-center w-full my-8'>
                <MdImageNotSupported className='text-6xl text-gray-500' />
                <h3 className='text-xl text-gray-500'>No images available</h3>
            </section>
        </>
    )
}

export default NoImages