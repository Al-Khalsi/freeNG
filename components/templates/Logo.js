import React from 'react'
import Link from 'next/link';

function Logo() {
    return (
        <Link href='/' className='w-20 h-20 flex justify-start items-center'>
            <img src="../../img/LOGO.png" className='w-12 h-12 object-cover 
            bg-gradient-to-t from-bgPurple to-bgLightPurple rounded-md' alt="Logo" title='Logo' />
        </Link>
    )
}

export default Logo