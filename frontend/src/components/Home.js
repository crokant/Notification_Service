import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import '../App.css';

const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function Home() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await fetch(`${apiUrl}/api/v1/health`);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const result = await response.json();
                setData(result);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        }

        fetchData();
    }, []);

    return (
        <div className="container">
            <h1>Добро пожаловать на наш сервис корпоративной рассылки!</h1>
            <p>
                Мы предоставляем удобный инструмент для управления рассылками и взаимодействия с вашей аудиторией.
                Создавайте рассылки, добавляйте участников и отправляйте сообщения в пару кликов.
            </p>

            {loading ? (
                <p>Загрузка...</p>
            ) : error ? (
                <p>Ошибка: {error}</p>
            ) : (
                data && (
                    <div className="message-box">
                        <p>
                            <strong>Сообщение:</strong> {data.message}
                        </p>
                        <p>
                            <strong>Источник:</strong> {data.origin}
                        </p>
                    </div>
                )
            )}

            <div className="cta-buttons">
                <Link to="/personal-office" className="cta-button">
                    Перейти в личный кабинет
                </Link>
                <a
                    href="https://github.com/crokant/Notification_Service/tree/main"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="cta-button secondary"
                >
                    GitHub проекта
                </a>
            </div>
        </div>
    );
}

export default Home;