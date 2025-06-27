FROM openjdk:21-jdk-slim

WORKDIR /app

COPY did-ta-server/gradlew .
COPY did-ta-server/gradle gradle
COPY did-ta-server/build.gradle .
COPY did-ta-server/settings.gradle .
COPY did-ta-server/libs libs
COPY did-ta-admin /did-ta-admin
COPY did-ta-server/src src

RUN chmod +x ./gradlew

ENV GRADLE_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
RUN ./gradlew bootJar --no-daemon --max-workers=1

CMD ["java", "-jar", "/app/build/libs/did-ta-server-2.0.0.jar"]