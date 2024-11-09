import { createContext, useContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';


const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState(null); // New state for username
    const [email, setEmail] = useState(null); // New state for email
    const [userId, setUserId] = useState(null); // New state for user ID
    const [role, setRole] = useState(null); // State for role

    useEffect(() => {
        // Loading token, username, and email from cookies when loading component
        const storedToken = Cookies.get('token');
        if (storedToken) {
            setToken(storedToken);

            // Decode the token to extract user information
            const decodedToken = JSON.parse(atob(storedToken.split('.')[1]));
            setUsername(decodedToken.username); // Extract username
            setEmail(decodedToken.email); // Extract email
            setRole(decodedToken.role); // Extract role from token
            // If userId is not in the token, you should fetch it from your backend
        }
    }, []);

    const storeToken = (newToken, newUserId) => {
        setToken(newToken);
        const decodedToken = JSON.parse(atob(newToken.split('.')[1]));
        setUsername(decodedToken.username);
        setEmail(decodedToken.email);
        setRole(decodedToken.role); // Store role from token
        setUserId(newUserId); // Store user ID separately
        Cookies.set('token', newToken);
    };

    const clearToken = () => {
        setToken(null);
        setUsername(null); // Clear username
        setEmail(null); // Clear email
        setUserId(null); // Clear user ID
        setRole(null); // Clear role
        Cookies.remove('token'); // Remove the token from the cookie
    };

    return (
        <AuthContext.Provider value={{ token, userId, setUserId, username, email, role, storeToken, clearToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};