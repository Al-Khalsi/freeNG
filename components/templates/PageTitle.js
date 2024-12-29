import React from 'react'

function PageTitle({title, totalElements}) {
    return (
        <div className='w-full py-12 px-8 flex-grow'>
            <div className='subject-text relative w-full text-center'>
                <h1 className='relative text-2xl md:text-4xl lg:text-6xl text-clLightPurple'>
                    {title}
                    <sub className='ml-1 lg:ml-2 text-base md:text-xl lg:text-3xl'>({totalElements})</sub>
                </h1>
            </div>
        </div>
    )
}

export default PageTitle