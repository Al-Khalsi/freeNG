import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);

    const storeToken = (newToken) => {
        setToken(newToken);
    };

    const clearToken = () => {
        setToken(null);
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