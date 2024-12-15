import React from 'react'

function Spinner() {
    return (
        <>
            <section className='loading flex justify-center w-full my-8 py-1'>
                <div className="loader relative w-20 h-20 rounded-lg overflow-hidden bg-white"></div>
            </section>
        </>
    )
}

export default Spinner