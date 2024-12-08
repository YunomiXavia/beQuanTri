FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/beQuanTri-0.0.1-SNAPSHOT.jar /app/beQuanTri.jar
COPY src/main/resources /app/src/main/resources

EXPOSE 8081

ENV jwt.signerKey="1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij"

ENTRYPOINT ["java", "-jar", "/app/beQuanTri.jar"]