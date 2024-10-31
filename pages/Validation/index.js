import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { signIn } from 'next-auth/react';
import { FaUser, FaLock } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import { MdEmail } from "react-icons/md";
import { useAuth } from '../../context/AuthContext'; // Adjust the path as necessary
import withAuthRedirect from '../../utils/withAuthRedirect'; // Adjust the path as necessary

function AuthForm() {
    const { token, storeToken } = useAuth(); // Adjusted to storeToken
    const [isActive, setIsActive] = useState(false);
    const [credentials, setCredentials] = useState({ username: '', email: '', password: '' });
    const [error, setError] = useState('');
    const router = useRouter();

    // Effect to prevent navigation back to login/register if already logged in
    useEffect(() => {
        if (token) {
            // If the user is logged in, redirect to home
            router.push('/');
        }
    }, [token, router]);

    const handleClick = (action) => {
        setIsActive(action === "register");
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredentials({ ...credentials, [name]: value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        const response = await fetch('http://localhost:3001/users' , { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: credentials.username,
                password: credentials.password,
            }),
        });

        if (response.ok) {
            const data = await response.json();
            const userToken = data.token; 
            const userId = data.userId;

            // Decode the token if necessary (assuming it's a JWT)
            const decodedToken = JSON.parse(atob(userToken.split('.')[1]));

            // Store token and userId
            storeToken(userToken, userId)
            console.log('Login successful');
            router.push('/');
        } else {
            setError('Invalid username or password');
        }

    };

    const handleRegistration = async (e) => {
        e.preventDefault();

        // Check for existing username or email
        const response = await fetch('http://localhost:3001/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: credentials.username,
                email: credentials.email,
                password: credentials.password,
            }),
        });

        if (existingUser) {
            setError('Username or email already exists');
            return; // Stop the registration process
        }


        if (response.ok) {
            const data = await response.json();
            const userToken = data.token;
            const userId = data.userId;

            // Decode the token if necessary
            const decodedToken = JSON.parse(atob(userToken.split('.')[1]));

             // Store token and userId
            storeToken(userToken);
            console.log('Registration successful:', data);
            router.push('/');
        } else {
            setError('Registration failed');
        }
    };

    return (
        <div className='Validation w-full h-full flex justify-center items-center min-h-screen bg-gradient-to-r from-lightGray to-lightBlue'>
            <div className={`wrapper relative w-custom-212 h-custom-136 m-5 bg-white rounded-3xl shadow-xl overflow-hidden ${isActive ? 'active' : ''}`}>
                <div className='login form-box absolute right-0 w-1/2 h-full flex items-center p-10 bg-white text-center z-10'>
                    <form onSubmit={handleLogin} className='w-full'>
                        <h2 className='text-4xl my-2.5'>Login</h2>
                        {error && <p className='text-red-500'>{error}</p>}
                        <div className="inputBox relative my-7 ">
                            <input type="text"
                                name="username"
                                value={credentials.username}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                name="password"
                                value={credentials.password}
                                onChange={handleChange}
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
                    <form onSubmit={handleRegistration} className='w-full'>
                        <h2 className='text-4xl my-2.5'>Register</h2>
                        {error && <p className='text-red-500'>{error}</p>}
                        <div className="inputBox relative my-7">
                            <input type="text"
                                name="username"
                                value={credentials.username}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Username' required />
                            <FaUser className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7 ">
                            <input type="email"
                                name="email"
                                value={credentials.email}
                                onChange={handleChange}
                                className='w-full py-3 pr-12 pl-5 bg-inputwhite rounded-lg border-none outline-none text-base font-medium'
                                placeholder='Email' required />
                            <MdEmail className='absolute right-5 top-1/2 -translate-y-1/2 text-gray-500' />
                        </div>
                        <div className="inputBox relative my-7">
                            <input type="password"
                                name="password"
                                value={credentials.password}
                                onChange={handleChange}
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
    );
}

export default withAuthRedirect(AuthForm);