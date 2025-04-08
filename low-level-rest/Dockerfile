FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle wrapper
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew assemble --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 6060
CMD ["java", "-jar", "app.jar"]
