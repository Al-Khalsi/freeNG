import React from 'react'
import { FaUser, FaLock } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import { MdEmail } from "react-icons/md";

function index() {
    return (
        <div className='Validaion flex justify-center items-center min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className='contianer relative w-custom-212 h-custom-136 bg-white rounded-3xl shadow-xl'>
                <div className='login absolute right-0 w-1/2 h-full flex items-center p-10 bg-white text-center'>
                    <form action="#" className='w-full'>
                        <h2 className='text-4xl my-2.5'>Login</h2>
                        <div className="inputBox relative my-7 ">
                            <input type="text"
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium placeholder-font-normal'
                                placeholder='Password' required />
                            <FaLock className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className='forgot-link -mt-3.5 mb-3.5'>
                            <p className='text-base no-underline'>Forgot password?</p>
                        </div>
                        <button type='submit' className='btn w-full h-12 rounded-lg bg-darkBlue text-white shadow-lg border-none text-base font-semibold cursor-pointer'>Login</button>
                        <p className='text-base my-4'>or login with</p>
                        <div className='social-icon'>
                            <p><FcGoogle /></p>
                        </div>
                    </form>
                </div>

                <div className='register absolute right-0 w-1/2 h-full hidden items-center bg-white text-center'>
                    <form action="#" className='w-full'>
                        <h2 className='text-4xl my-2.5'>Login</h2>
                        <div className="inputBox relative my-7 ">
                            <input type="text"
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7 ">
                            <input type="email"
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Email' required />
                            <MdEmail className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium placeholder-font-normal'
                                placeholder='Password' required />
                            <FaLock className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <button type='submit'
                            className='btn w-full h-12 rounded-lg bg-darkBlue text-white shadow-lg border-none text-base font-semibold cursor-pointer'>
                            Register
                        </button>
                        <p className='text-base my-4'>or register with</p>
                        <div className='social-icon'>
                            <p><FcGoogle /></p>
                        </div>
                    </form>
                </div>

                <div className="toggle-form absolute w-full h-full">
                    <div className='toggle-panel absolute w-1/2 h-full flex flex-col justify-center items-center text-white bg-darkBlue'>
                        <h4 className='text-3xl mb-2'>Hello, Welcome!</h4>
                        <p className='mb-5'>Don't have an account?</p>
                        <button className='btn w-40 h-12 bg-transparent border-2 rounded-lg text-base font-semibold cursor-pointer'>Register</button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default index