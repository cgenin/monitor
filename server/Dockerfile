FROM openjdk:8-jdk-alpine
VOLUME /tmp
VOLUME /nitrite
VOLUME /logs
ARG JAR_FILE
ARG CONFIG_FILE
ADD ${JAR_FILE} app.jar
ADD ${CONFIG_FILE} config.json
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar", "-conf", "/config.json"]