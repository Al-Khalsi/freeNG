import { createContext, useContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState(null); // State for username
    const [email, setEmail] = useState(null); // State for email
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
        }
    }, []);

    const storeToken = (newToken) => {
        setToken(newToken);
        const decodedToken = JSON.parse(atob(newToken.split('.')[1]));
        setUsername(decodedToken.username); // Store username from token
        setEmail(decodedToken.email); // Store email from token
        setRole(decodedToken.role); // Store role from token
        Cookies.set('token', newToken); // Store token in cookies
    };

    const clearToken = () => {
        setToken(null);
        setUsername(null); // Clear username
        setEmail(null); // Clear email
        setRole(null); // Clear role
        Cookies.remove('token'); // Remove the token from the cookie
    };

    return (
        <AuthContext.Provider value={{ token, username, email, role, storeToken, clearToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
    // این برای تست نوشته شده مستر محمد حسن، بزار باشه من برای تست ازش استفاده میکنم.
    // const context = useContext(AuthContext);
    // const testToken = 'eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJQTkctRG93bmxvYWRpbmctUGxhdGZvcm0iLCJzdWIiOiJzdHJpbmciLCJlbWFpbCI6InN0cmluZyIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwidXNlcm5hbWUiOiJzdHJpbmciLCJpYXQiOjE3MzIzNTgxOTEsImV4cCI6MTczMjQ0NDU5MX0.5s-RNFLKZ2NrwJV1fFJoY-bwm2zc4ByQ9h3ebyQ1938';
    // return {...context, token: testToken}; // Override the token
};