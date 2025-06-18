#FROM ubuntu:latest
#LABEL authors="Дарья"
#
#ENTRYPOINT ["top", "-b"]

# Используем официальный образ OpenJDK
# Этап сборки
FROM maven:3.8.6-openjdk-17-slim AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml .
# Скачиваем зависимости (этот шаг кэшируется)
RUN mvn dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение с профилем "docker"
RUN mvn clean package -DskipTests -Pdocker

# Финальный образ
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем собранный JAR из builder
COPY --from=builder /app/target/spring_boot_security-*.jar app.jar

# Копируем статические ресурсы (HTML, JS, CSS)
COPY --from=builder /app/src/main/resources/static /app/static

# Параметры для оптимизации работы Hibernate
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Открываем порт приложения
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
