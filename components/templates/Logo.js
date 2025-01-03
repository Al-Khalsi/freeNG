import React from 'react'
import Link from 'next/link';
import Image from 'next/image';

function Logo() {
    return (
        <Link href='/' className='relative w-14 h-14 flex justify-start items-center'>
            <Image
                src="/img/LOGO.png"
                className='object-cover rounded-md
                bg-gradient-to-t from-bgPurple to-bgLightPurple'
                layout="fill"
                title="Logo"
                alt="Logo"
            />
        </Link>
    )
}

export default Logo