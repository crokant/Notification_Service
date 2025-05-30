# Этап сборки
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и зависимости для кеширования сборки
COPY pom.xml ./
RUN mvn dependency:resolve

# Копируем исходный код проекта
COPY src ./src

# Собираем проект с помощью Maven (создаем JAR файл)
RUN mvn clean install -X

# Этап продакшн: используем OpenJDK 17 для выполнения приложения
FROM openjdk:17-jdk-slim

# Копируем собранный JAR файл из предыдущего этапа
COPY --from=build /app/target/*.jar /app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app.jar"]