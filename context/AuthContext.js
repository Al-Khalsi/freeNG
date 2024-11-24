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
   // /* این برای تست نوشته شده مستر محمد حسن، بزار باشه من برای تست ازش استفاده میکنم. */
   //  const context = useContext(AuthContext);
   //  const testToken = 'eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJQTkctRG93bmxvYWRpbmctUGxhdGZvcm0iLCJzdWIiOiJzZXllZC5hbGkuZGV2bEBnbWFpbC5jb20iLCJlbWFpbCI6InNleWVkLmFsaS5kZXZsQGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjoiUk9MRV9NQVNURVIiLCJ1c2VybmFtZSI6IlNleWVkIEFsaSIsImlhdCI6MTczMjQzMjk4NSwiZXhwIjoxNzMyNTE5Mzg1fQ.FQtKVKkk3b9z1R0BqAa36MczNN9TS8DjKV4aQCnsU5E';
   //  return {...context, token: testToken}; // Override the token
};