import React from 'react'
import { FaUser, FaLock, FaGoogle, FaGithub, FaLinkedin } from "react-icons/fa";

function index() {
    return (
        <div className='Validaion flex justify-center items-center min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className='contianer relative w-custom-212 h-custom-136 bg-white rounded-3xl shadow-xl'>
                <div className='form-box login absolute right-0 w-1/2 h-full flex items-center bg-darkGray text-center'>
                    <form action="#" className='w-full'>
                        <h2 className='text-4xl my-2.5'>Login</h2>
                        <div className="inputBox relative my-7 ">
                            <input type="text" 
                            className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium' 
                            placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500'/>
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password" 
                            className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium placeholder-font-normal' 
                            placeholder='Password' required />
                            <FaLock className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500'/>
                        </div>
                        <div className='forgot-link -mt-3.5 mb-3.5'>
                            <p className='text-base bg-darkGray no-underline'>Forgot password?</p>
                        </div>
                        <button type='submit' className='btn w-full h-12 rounded-lg bg-sky-400 shadow-lg border-none cursor-pointer text-base bg-white font-semibold'>Login</button>
                        <p className='text-base my-4'>or login with</p>
                        <div className='social-icon'>
                            <p><FaGoogle /></p>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default index