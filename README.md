
# Сервис управления уведомлениями
<img src="https://i.ibb.co/vzpc6RQ/c6b9ac5c-f494-4bfe-b1ce-5caf031092fd.webp" alt="Логотип" />

## Описание
Notification_Service - это сервис для управления уведомлениями. 
Он позволяет отправлять уведомления пользователям по различным каналам, таким как email, SMS, push-уведомления и т.д.

## Функциональность
- Отправка email уведомлений
- Отправка SMS уведомлений
- Отправка push-уведомлений
- Поддержка шаблонов уведомлений
- Настройка расписания отправки уведомлений
- 
## Swagger OpenApi доступен по адресу 
http://localhost:8080/swagger-ui

## Использование
После запуска приложение будет доступно по адресу `http://localhost:8000`. Вы можете использовать API для отправки уведомлений. Примеры запросов:

### Отправка email уведомления
```http
POST /send-email
Content-Type: application/json

{
    "to": "recipient@example.com",
    "subject": "Test Email",
    "body": "This is a test email."
}
```

### Отправка SMS уведомления
```http
POST /send-sms
Content-Type: application/json

{
    "to": "+1234567890",
    "message": "This is a test SMS."
}
```

### Отправка push-уведомления
```http
POST /send-push
Content-Type: application/json

{
    "to": "push-token",
    "title": "Test Push",
    "body": "This is a test push notification."
}
```

## Структура проекта
- `backend/` - тут все что нужно для развертывания сервиса 
- `frontend/` - тут все что нужно для того чтобы сервис выглядел красиво
- `docker-compose.yml` - этот файл отвечает за развертывания на докер


## Лицензия
Этот проект лицензируется под лицензией BSD License. Более подробную информацию смотрите в файле LICENSE.
