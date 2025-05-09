import React, {useContext, useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {fetchWithAuth, postWithAuth} from '../utils/api'; // Утилита для запросов с авторизацией
import {AuthContext} from './AuthContext'; // Импортируем контекст

const PersonalOffice = () => {
    const [userInfo, setUserInfo] = useState(null);
    const [mailings, setMailings] = useState([]); // Список рассылок
    const [loading, setLoading] = useState(true);
    const [newMailing, setNewMailing] = useState({ title: '', description: '', emails: '' }); // Форма для новой рассылки
    const navigate = useNavigate();
    const { setIsAuthenticated } = useContext(AuthContext);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
        } else {
            fetchData();
        }
    }, [navigate]);

    const fetchData = async () => {
        try {
            const userResponse = await fetchWithAuth('api/user/info', { method: 'GET' });
            if (userResponse.ok) {
                const userData = await userResponse.json();
                setUserInfo(userData);
            } else if (userResponse.status === 401) {
                localStorage.removeItem('token');
                navigate('/login');
            } else {
                console.error('Ошибка при загрузке информации о пользователе:', userResponse.status);
            }

            const mailingsResponse = await fetchWithAuth('api/user/mailings', { method: 'GET' });
            if (mailingsResponse.ok) {
                const mailingsData = await mailingsResponse.json();
                setMailings(mailingsData);
            } else if (mailingsResponse.status === 401) {
                localStorage.removeItem('token');
                navigate('/login');
            } else {
                console.error('Ошибка при загрузке списка рассылок:', mailingsResponse.status);
            }
        } catch (error) {
            console.error('Ошибка сети:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateMailing = async (e) => {
        e.preventDefault();
        try {

            // Преобразуем список почт в массив
            const emailsArray = newMailing.emails
                .split(/[\s,]+/) // Разделяем по пробелам или запятым
                .filter((email) => email.trim() !== ''); // Убираем пустые строки

            // Отправляем данные на сервер
            const response = await postWithAuth('/api/subscriptions/create', {
                ...newMailing,
                emails: emailsArray, // Отправляем массив почт
            });

            if (response.ok) {
                const newMailingData = await response.json();
                setMailings([...mailings, newMailingData]); // Добавляем новую рассылку в список
                setNewMailing({ title: '', description: '', emails: '' }); // Очищаем форму
            } else {
                console.error('Ошибка при создании рассылки:', response.status);
            }
        } catch (error) {
            console.error('Ошибка сети:', error);
        }
    };

    if (loading) {
        return <p>Загрузка...</p>;
    }

    if (!userInfo) {
        return <p>Ошибка загрузки данных. Попробуйте позже.</p>;
    }

    return (
        <div className="container">
            <h1>Личный кабинет</h1>
            <p>Добро пожаловать, {userInfo.name}!</p>


            <div className="office-content">
                {/* Список рассылок */}
                <div className="mailings-list">
                    <h2>Мои рассылки</h2>
                    {mailings.length > 0 ? (
                        <ul>
                            {mailings.map((mailing) => (
                                <li key={mailing.id}>
                                    <strong>{mailing.title}</strong>
                                    <p>{mailing.description}</p>
                                    <p>Участники: {mailing.emails.join(', ')}</p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <ul>

                            {/* тестовая рассылка */}
                        <li key="1">
                            <strong>Информация по курсу: Информатика (организация и поиск данных)</strong>
                            <p>Автор: Рословцев В.В</p>
                            <p>Участники: Амбарнов, Бекетов, Блувштейн ... </p>
                        </li>
                        <li key="2">
                            <strong>Информация по курсу:  СГЛА </strong>
                            <p>Автор: Камынин В.Л</p>
                            <p>Участники: Амбарнов, Бекетов, Блувштейн ... </p>
                        </li>
                        <li key="3">
                            <strong>Информация по курсу:  Математический анализ</strong>
                            <p>Автор: Теляковский Д.С</p>
                            <p>Участники: Амбарнов, Бекетов, Блувштейн ... </p>
                        </li>
                            {/*<p>У вас пока нет рассылок.</p>*/}
                        </ul>
                        )
                    }
                </div>

                {/* Форма для создания новой рассылки */}
                <div className="create-mailing">
                    <h2>Создать новую рассылку</h2>
                    <form onSubmit={handleCreateMailing}>
                        <label>
                        Название:
                            <input
                                type="text"
                                value={newMailing.title}
                                onChange={(e) => setNewMailing({ ...newMailing, title: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            Описание:
                            <textarea
                                value={newMailing.description}
                                onChange={(e) => setNewMailing({ ...newMailing, description: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            Почты участников (через запятую или пробел):
                            <input
                                type="text"
                                value={newMailing.emails}
                                onChange={(e) => setNewMailing({ ...newMailing, emails: e.target.value })}
                                placeholder="user1@example.com, user2@example.com"
                            />
                        </label>
                        <button type="submit">Создать</button>
                    </form>
                </div>
            </div>

            <button
                className="logout-button"
                onClick={() => {
                    localStorage.removeItem('token'); // Удаляем токен
                    setIsAuthenticated(false);
                    navigate('/login'); // Перенаправляем на страницу входа
                }}
            >
                Выйти
            </button>
        </div>
    );
};

export default PersonalOffice;