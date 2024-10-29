import { createContext, useContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState(null); // New state for username
    const [email, setEmail] = useState(null); // New state for email
    const [userId, setUserId] = useState(null); // New state for user ID

    useEffect(() => {
        // Loading token, username, and email from cookies when loading component
        const storedToken = Cookies.get('token');
        const storedUsername = Cookies.get('username');
        const storedEmail = Cookies.get('email'); // Load email from cookies
        const storedUserId = Cookies.get('userId'); // Load user ID from cookies
        if (storedToken) {
            setToken(storedToken);
        }
        if (storedUsername) {
            setUsername(storedUsername);
        }
        if (storedEmail) {
            setEmail(storedEmail); // Set email from cookies
        }
        if (storedUserId) setUserId(storedUserId); // Set user ID from cookies
    }, []);

    const storeToken = (newToken, newUsername, newEmail, newUserId) => {
        setToken(newToken);
        setUsername(newUsername); // Store username
        setEmail(newEmail); // Store email
        setUserId(newUserId); // Store user ID
        Cookies.set('token', newToken); // Save token in cookie
        Cookies.set('username', newUsername); // Save username in cookie
        Cookies.set('email', newEmail); // Save email in cookie
        Cookies.set('userId', newUserId); // Save user ID in cookie
    };

    const clearToken = () => {
        setToken(null);
        setUsername(null); // Clear username
        setEmail(null); // Clear email
        setUserId(null); // Clear user ID
        Cookies.remove('token'); // Remove the token from the cookie
        Cookies.remove('username'); // Remove username from cookie
        Cookies.remove('email'); // Remove email from cookie
        Cookies.remove('userId'); // Remove user ID from cookie
    };

    return (
        <AuthContext.Provider value={{ token, username, email, userId, storeToken, clearToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};