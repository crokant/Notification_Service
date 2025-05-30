import React from 'react';
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';
import {AuthProvider} from './components/AuthContext';
import AuthLinks from "./components/AuthLinks"
import Home from './components/Home';
import About from './components/About';
import ApiDocumentation from './components/ApiDocumentation';
import Register from './components/Register';
import Login from './components/Login';
import PersonalOffice from './components/PersonalOffice';
import ProtectedRoute from "./components/ProtectedRoute";
import Business from "./components/Business";
import './App.css';

const App = () => {
    return (
        <AuthProvider>
            <Router>
                <div className="navbar">
                    <nav>
                        <ul className="nav-links">
                            <li>
                                <Link to="/">Главная</Link>
                            </li>
                            <li>
                                <Link to="/about">О нас</Link>
                            </li>
                            <li>
                                <Link to="/docks">Документация API</Link>
                            </li>
                            <li>
                                <Link to="/business">Для бизнеса</Link>
                            </li>
                        </ul>
                        <AuthLinks /> {/* Перемещаем логику авторизации в отдельный компонент */}
                    </nav>
                </div>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/about" element={<About />} />
                    <Route path="/docks" element={<ApiDocumentation />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/business" element={<Business />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/personal-office" element={<PersonalOffice />} />
                    </Route>

                </Routes>
            </Router>
        </AuthProvider>
    );
};

export default App;