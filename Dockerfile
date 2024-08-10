FROM azul/zulu-openjdk-alpine:21.0.1

VOLUME /tmp

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
