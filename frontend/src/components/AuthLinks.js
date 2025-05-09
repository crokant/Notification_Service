import React, {useContext} from 'react';
import {Link} from 'react-router-dom';
import {AuthContext} from './AuthContext';

const AuthLinks = () => {
    const { isAuthenticated } = useContext(AuthContext);

    return (
        <ul className="auth-links">
            {isAuthenticated ? (
                <li>
                    <Link to="/personal-office">Личный кабинет</Link>
                </li>
            ) : (
                <>
                    <li>
                        <Link to="/login">Вход</Link>
                    </li>
                    <li>
                        <Link to="/register">Регистрация</Link>
                    </li>
                </>
            )}
        </ul>
    );
};

export default AuthLinks;