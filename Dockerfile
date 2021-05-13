#Build:  docker build -t nassiesse/simple-java-ocr .
#Run: docker run -t -i -p 8080:8080 nassiesse/simple-java-ocr


FROM openjdk

# Refer to Maven build -> finalName
ARG JAR_FILE=target/PDFAPIMicroservice-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
