FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY target/*.jar MyJAAS.jar
COPY ../target/classes/static/keystore.p12 keystore.p12
ENTRYPOINT ["java", "-jar", "/app/MyJAAS.jar"]