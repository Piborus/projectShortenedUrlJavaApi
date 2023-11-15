#FROM openjdk:17
#COPY out/artifacts/projectShortenedUrlJavaApi_jar/projectShortenedUrlJavaApi.jar /app/myapp.jar
#WORKDIR /app
#RUN apt-get update && apt-get install -y postgresql-client
#CMD ["java", "-jar", "myapp.jar"]

#FROM openjdk:17
#ARG JAR_FILE=out/artifacts/projectShortenedUrlJavaApi_jar/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
#EXPOSE 8080:8080

#FROM maven:3-openjdk-17 as build
#RUN mkdir -p /shorterUrl/src/app
#WORKDIR /shorterUrl/src/app
#ADD . /shorterUrl/src/app
#RUN mvn package
#
#FROM eclipse-temurin:17-jdk
#RUN mkdir -p /shorterUrl/src/app
#WORKDIR /shorterUrl/src/app
#COPY --from=build /shorterUrl/src/app/target/shorter.jar app.jar
#CMD ["java","-jar","/app.jar"]

## Use Maven to build the application
#FROM maven:3-openjdk-17 as build
#WORKDIR /shorterUrl
#COPY ../../.. .
#RUN mvn package
#
## Use the JRE image to run the application
#FROM eclipse-temurin:17-jdk
#WORKDIR /app
#COPY --from=build /shorterUrl/target/shorter.jar app.jar
#CMD ["java", "-jar", "app.jar93-"]

FROM eclipse-temurin:17-jre-ubi9-minimal
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
