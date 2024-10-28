import { createContext, useContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState(null); // New state for username

    useEffect(() => {
        // Loading token and username from cookies when loading component
        const storedToken = Cookies.get('token');
        const storedUsername = Cookies.get('username');
        if (storedToken) {
            setToken(storedToken);
        }
        if (storedUsername) {
            setUsername(storedUsername);
        }
    }, []);

    const storeToken = (newToken, newUsername) => {
        setToken(newToken);
        setUsername(newUsername); // Store username
        Cookies.set('token', newToken); // Save token in cookie
        Cookies.set('username', newUsername); // Save username in cookie
    };

    const clearToken = () => {
        setToken(null);
        setUsername(null); // Clear username
        Cookies.remove('token'); // Remove the token from the cookie
        Cookies.remove('username'); // Remove username from cookie
    };

    return (
        <AuthContext.Provider value={{ token, username, storeToken, clearToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};