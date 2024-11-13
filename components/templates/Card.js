import React from 'react'
import { MdFullscreen } from "react-icons/md";

function Card({ image }) {
    return (
        <div className='card w-full h-80 rounded-lg overflow-hidden bg-bgDarkGray'>
            <div className='inside-card h-full w-full p-3'>
                <div className='bg-img relative w-full h-2/3 flex justify-center items-center p-2 rounded-md'>
                    <div className='absolute top-2 right-2 bg-darkBlue text-white p-1 rounded-md opacity-60'>
                        <MdFullscreen className='text-xl' />
                    </div>
                    <img src={image.Src} alt={image.Title} className='w-full h-full object-continer' />
                </div>
                <div className='info-img w-full h-1/3 px-2 py-3'>
                    <h3 className='block text-xl text-white text-ellipsis overflow-hidden whitespace-nowrap'>{image.Title}</h3>
                    <div className='flex justify-between mt-3'>
                        <div className='flex flex-col w-1/3 text-center pr-2 text-lightBlue'>
                            <span className='block text-sm'>{image.Size}</span>
                            <span className='text-xs'>Size</span>
                        </div>
                        <div className='flex flex-col w-1/3 text-center text-lightBlue border-x-2 border-lightGray'>
                            <span className='block text-sm'>{image.Dimensions}</span>
                            <span className='text-xs'>Dimensions</span>
                        </div>
                        <div className='flex flex-col w-1/3 text-center pl-2 text-lightBlue'>
                            <span className='block text-sm'>{image.Download}</span>
                            <span className='text-xs'>Download</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Card