FROM openjdk:8-jdk-alpine

# Refer to Maven build -> finalName
ARG JAR_FILE=target/PDFAPIMicroservice-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar -Xmx512m /opt/app/app.jar
ENTRYPOINT ["java","-jar", "-Xms512m", "-Xmx1152m", "-XX:MaxPermSize=256m", "-XX:MaxNewSize=256m","app.jar"]
