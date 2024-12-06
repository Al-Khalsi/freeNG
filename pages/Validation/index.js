import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { FaUser, FaLock } from "react-icons/fa";
import { MdEmail } from "react-icons/md";
import { useAuth } from '@/context/AuthContext'; // Adjust the path as necessary
import withAuthRedirect from '@/utils/withAuthRedirect'; // Adjust the path as necessary
import jwt_decode from 'jwt-decode';
import axios from 'axios'; // Import Axios

function AuthForm() {
    const { token, storeToken, setUsername, setEmail, setRole } = useAuth(); // Added setUsername
    const [isActive, setIsActive] = useState(false);
    const [credentials, setCredentials] = useState({ username: '', email: '', password: '' });
    const [error, setError] = useState('');
    const router = useRouter();

    useEffect(() => {
        if (token) {
            router.push('/'); // Redirect to home if token exists
        }
    }, [token, router]);

    const handleClick = (action) => {
        setIsActive(action === "register"); // Toggle between login and registration
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredentials({ ...credentials, [name]: value }); // Update credentials state
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
                email: credentials.email,
                password: credentials.password,
            });

            // Log the entire response and response.data
            console.log('Response:', response); // Log the entire response object
            console.log('Response Data:', response.data); // Log the data part of the response

            const data = response.data.data; // Get response data
            const userToken = data.token; // Get user token
            const username = data.userDetails.username; // Extract username from response
            console.log(data); // Log the data received

            if (userToken) {
                storeToken(userToken); // Store the token

                // Decode the token
                const decodedToken = jwt_decode(userToken);
                console.log('Decoded Token:', decodedToken);

                // Store user information from token
                setUsername(username); // Store username from response
                setEmail(decodedToken.email); // Store email from token
                setRole(decodedToken.role); // Store role from token

                console.log('Login successful');
                router.push('/'); // Redirect to home after successful login
            } else {
                setError('Failed to retrieve token'); // Error handling
            }
        } catch (error) {
            // Improved error handling
            if (error.response) {
                console.error('Login error response:', error.response); // Log the entire error response
                console.error('Login error data:', error.response.data); // Log the data part of the error response
                setError('Invalid username or password'); // Error message for invalid credentials
            } else {
                console.error('Login error:', error.message); // Log the error message
                setError('An error occurred during login'); // Generic error message
            }
        }
    };

    const handleRegistration = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/register', {
                username: credentials.username,
                email: credentials.email,
                password: credentials.password,
            });

            console.log('Response:', response); // Log the entire response object
            console.log('Response Data:', response.data); // Log the data part of the response

            const data = response.data.data; // Get response data
            const userToken = data.token; // Get user token
            console.log(data); // Log the data received

            if (userToken) {
                storeToken(userToken); // Store the token

                // Decode the token
                const decodedToken = jwt_decode(userToken);
                console.log('Decoded Token:', decodedToken);

                // Store user information from token
                setUsername(credentials.username); // Store username from registration form
                setEmail(decodedToken.email); // Store email from token
                setRole(decodedToken.role); // Store role from token

                console.log('Registration successful:', data);
                router.push('/'); // Redirect to home after successful registration
            } else {
                setError('Failed to retrieve token'); // Error handling
            }
        } catch (error) {
            // Improved error handling
            if (error.response) {
                console.error('Registration error:', error.response); // Log the entire error response
                console.error('Registration error data:', error.response.data); // Log the data part of the error response
                setError(error.response.data.message || 'Registration failed'); // Set error message from response or default
            } else if (error.request) {
                console.error('No response received:', error.request); // Log request
                setError('No response from server'); // Set a generic error message
            } else {
                console.error('Error setting up request:', error.message); // Log error message
                setError('Error during registration'); // Set a generic error message
            }
        }
    };

    return (
        <div className='Validation w-full h-full flex justify-center items-center min-h-screen bg-bgDarkBlue'>
            <div className={`wrapper relative w-custom-212 h-custom-136 m-5 bg-bgDarkGray rounded-3xl shadow-xl overflow-hidden ${isActive ? 'active' : ''}`}>
                <div className='login form-box absolute right-0 w-1/2 h-full flex items-center p-10 bg-gradient-to-t from-bgPurple to-bgLightPurple text-center z-10'>
                    <form onSubmit={handleLogin} className='w-full'>
                        <h2 className='text-4xl my-2.5'>Login</h2>
                        {error && <p className='text-red-500'>{error}</p>}
                        <div className="inputBox relative my-7 ">
                            <input type="text"
                                name="email"
                                value={credentials.email}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-bgGray rounded-lg border-none outline-none text-clBlack text-base font-medium'
                                placeholder='Email' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                name="password"
                                value={credentials.password}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-bgGray rounded-lg border-none outline-none text-clBlack text-base font-medium placeholder-font-normal'
                                placeholder='Password' required />
                            <FaLock className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <button type='submit' className='btn w-full h-12 rounded-lg bg-bgDarkBlue text-white shadow-lg border-none text-base font-semibold cursor-pointer'>Login</button>
                        
                    </form>
                </div>

                <div className='register form-box absolute right-0 w-1/2 h-full flex items-center p-10 bg-gradient-to-t from-bgPurple to-bgLightPurple text-center z-10'>
                    <form onSubmit={handleRegistration} className='w-full'>
                        <h2 className='text-4xl my-2.5'>Register</h2>
                        {error && <p className='text-red-500'>{error}</p>}
                        <div className="inputBox relative my-7">
                            <input type="text"
                                name="username"
                                value={credentials.username}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-bgGray rounded-lg border-none outline-none text-clBlack text-base font-medium'
                                placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7 ">
                            <input type="email"
                                name="email"
                                value={credentials.email}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-bgGray rounded-lg border-none outline-none text-clBlack text-base font-medium'
                                placeholder='Email' required />
                            <MdEmail className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                name="password"
                                value={credentials.password}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-bgGray rounded-lg border-none outline-none text-clBlack text-base font-medium placeholder-font-normal'
                                placeholder='Password' required />
                            <FaLock className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <button type='submit'
                            className='btn w-full h-12 rounded-lg bg-bgDarkBlue text-white shadow-lg border-none text-clBlack text-base font-semibold cursor-pointer'>
                            Register
                        </button>
                    
                    </form>
                </div>

                <div className="toggle-form absolute left-0 w-full h-full bg-gradient-to-t from-bgPurple to-bgLightPurple">
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
    );
}

export default withAuthRedirect(AuthForm);