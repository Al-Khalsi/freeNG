import { useState } from 'react'
import { FaUser, FaLock } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import { MdEmail } from "react-icons/md";

function index() {

    const [isActive, setIsActive] = useState(false);


    const handleClick = (action) => {
        setIsActive(action === "register");
    };

    return (
        <div className='Validaion w-full h-full flex justify-center items-center min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className={`wrapper relative w-custom-212 h-custom-136 m-5 bg-white rounded-3xl shadow-xl overflow-hidden ${isActive ? 'active' : ''}`}>
                <div className='login form-box absolute right-0 w-1/2 h-full flex items-center p-10 bg-white text-center z-10'>
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
                        <p className='text-base my-4'>or</p>
                        <div className='social-icon'>
                            <p className='w-full h-12 rounded-lg flex justify-center items-center bg-lightGray shadow-lg text-lg'><FcGoogle className='mr-2 text-2xl' />Login With Google</p>
                        </div>
                    </form>
                </div>

                <div className='register form-box absolute right-0 w-1/2 h-full flex items-center p-10 bg-white text-center z-10'>
                    <form action="#" className='w-full'>
                        <h2 className='text-4xl my-2.5'>Register</h2>
                        <div className="inputBox relative my-7">
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
                        <p className='text-base my-4'>or</p>
                        <div className='social-icon'>
                            <p className='w-full h-12 rounded-lg flex justify-center items-center bg-lightGray shadow-lg text-lg'><FcGoogle className='mr-2 text-2xl' /> Register With Google</p>
                        </div>
                    </form>
                </div>

                <div className="toggle-form absolute left-0 w-full h-full">
                    <div className='toggle-panel toggle-left absolute left-0 w-1/2 h-full flex flex-col justify-center items-center text-white z-20'>
                        <h4 className='text-3xl mb-2 hidden md:block'>Hello, Welcome!</h4>
                        <p className='mb-5 hidden md:block'>Don't have an account?</p>
                        <button className='btn w-40 h-12 bg-transparent border-2 rounded-lg text-base font-semibold cursor-pointer'
                            onClick={() => handleClick("register")}>Register</button>
                    </div>
                    <div className='toggle-panel toggle-right absolute -right-1/2 w-1/2 h-full flex flex-col justify-center items-center text-white z-20'>
                        <h4 className='text-3xl mb-2 hidden md:block'>Welcome Back!</h4>
                        <p className='mb-5 hidden md:block'>Already have an account?</p>
                        <button className='btn w-40 h-12 bg-transparent border-2 rounded-lg text-base font-semibold cursor-pointer'
                            onClick={() => handleClick("login")}>Login</button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default index