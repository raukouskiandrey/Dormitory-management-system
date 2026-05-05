# ---------- СТАДИЯ 1: СБОРКА ----------
# Берём Java 25 и ставим Maven сами
FROM eclipse-temurin:25-jdk AS build

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Кэш зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# ---------- СТАДИЯ 2: ЗАПУСК ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Копируем jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]