FROM openjdk:11-jre-slim

# Refer to Maven build -> finalName
ARG JAR_FILE=target/PDFAPIMicroservice-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar -Xmx512m /opt/app/app.jar
ENTRYPOINT ["java","-jar", "-Xms4g", "-Xmx4g", "app.jar"]
