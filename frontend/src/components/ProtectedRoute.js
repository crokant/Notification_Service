import React, {useContext} from 'react';
import {Navigate, Outlet} from 'react-router-dom';
import {AuthContext} from './AuthContext';

const ProtectedRoute = () => {
    const { isAuthenticated } = useContext(AuthContext);

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;