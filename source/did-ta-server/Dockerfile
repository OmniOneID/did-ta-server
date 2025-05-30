FROM openjdk:21-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY libs libs
COPY sample sample
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew bootJar

CMD ["java", "-jar", "/app/build/libs/Tas.jar"]