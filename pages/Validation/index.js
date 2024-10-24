import React from 'react'
import { FaUser, FaLock, FaGoogle, FaGithub, FaLinkedin } from "react-icons/fa";

function index() {
  return (
    <div className='Validaion flex justify-center items-center min-h-screen'>
        <div className='form-box login'>
            <form action="#">
                <h2>Login</h2>
                <div className="inputBox">
                    <input type="text" placeholder='Username' required />
                    <FaUser />
                </div>
                <div className="inputBox">
                    <input type="password" placeholder='Password' required />
                    <FaLock />
                </div>
                <div className='forgot-link'>
                    <p>Forgot password?</p>
                </div>
                <button type='submit' className='btn'>Login</button>
                <p>or login with</p>
                <div className='social-icon'>
                    <p><FaGoogle /></p>
                </div>
            </form>
        </div>
    </div>
  )
}

export default index