import React, {createContext, useEffect, useState} from 'react';

// Создаем контекст
export const AuthContext = createContext();

// Провайдер контекста
export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    // Проверяем токен при каждом рендере
    useEffect(() => {
        const token = localStorage.getItem('token');
        console.log("Checking token:", token); // Отладка
        if (token && token.trim() !== '') {
            setIsAuthenticated(true);
        } else {
            setIsAuthenticated(false);
        }
    }, []);

    const login = (token) => {
        localStorage.setItem('token', token);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('token');
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
