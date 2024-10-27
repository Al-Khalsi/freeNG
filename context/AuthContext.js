// context/AuthContext.js
import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);

    const storeToken = (newToken) => {
        setToken(newToken);
        localStorage.setItem('token', newToken); // Optionally store in localStorage
    };

    const clearToken = () => {
        setToken(null);
        localStorage.removeItem('token'); // Clear from localStorage
    };

    return (
        <AuthContext.Provider value={{ token, storeToken, clearToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};