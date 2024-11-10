import React from 'react'

function Footer() {
    return (
        <footer className='footer w-full text-white'>
            <div className='footer-content w-full flex flex-col'>
                <div className='left w-1/3 flex justify-between px-2 md:px-8'>
                    <div className='bg-logo'>
                        <div className="points_wrapper">
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                            <i className="point"></i>
                        </div>
                        <div className='logo'>
                            I<span className='burnt'>m</span>Alchem<span>y</span>
                        </div>
                    </div>
                    <div className='Logo-Description'></div>
                </div>
                <div className='center w-1/3 flex'></div>
                <div className='right w-1/3 flex'></div>
            </div>
        </footer>
    )
}

export default Footer