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

#FROM eclipse-temurin:17-jre-ubi9-minimal
#ARG JAR_FILE=docker/*.jar
#COPY ${JAR_FILE} application.jar
#ENTRYPOINT ["java", "-jar", "application.jar"]
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#
#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
#
#docker build --build-arg JAR_FILE=docker/shorterUrl-0.0.1-SNAPSHOT.jar -t myorg/myapp .


#FROM eclipse-temurin:17-jdk-alpine
#
## Volume para armazenar temporariamente dados
#VOLUME /tmp
#
## Argumento para o arquivo JAR (pode ser substituído durante a construção)
#ARG JAR_FILE=shorterUrl-0.0.1-SNAPSHOT.jar
#
## Copia o arquivo JAR para dentro da imagem com o nome 'app.jar'
#COPY ${JAR_FILE} app.jar
#
## Comando de entrada para executar o aplicativo Java quando o contêiner for iniciado
#ENTRYPOINT ["java", "-jar", "/app.jar"]


## Use an official Maven image as a parent image
#FROM eclipse-temurin:17-jdk-alpine AS build
#
## Set the working directory in the container
#WORKDIR /app
#
#ENV HOME=/shorterUrl
#RUN mkdir -p $HOME
#WORKDIR $HOME
#ADD . $HOME
#RUN --mount=type=cache,shorterUrl/mvnw -f $HOME/pom.xml clean package
#
#
#
## Use an official OpenJDK runtime as a parent image
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory in the container
#WORKDIR /app
#
#ARG JAR_FILE=/usr/app/target/*.jar
#COPY --from=build $JAR_FILE /app/shorterUrl-0.0.1-SNAPSHOT.jar
#EXPOSE 8080
#ENTRYPOINT java -jar /app/shorterUrl-0.0.1-SNAPSHOT.jar

## Use an official Maven image with Eclipse Temurin JDK 17 as a parent image for building
#FROM maven:3.8.4-openjdk-17-slim AS build
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the Maven Wrapper files
#COPY .mvn .mvn
#
## Copy the project files
#COPY . .
#
## Set permissions for Maven Wrapper
#RUN chmod +x .mvn/wrapper/maven-wrapper
#
## Build the project
#RUN ./mvnw clean install
#
## Use an official Eclipse Temurin JDK 17 runtime as a parent image for the final stage
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy only the necessary files from the build stage
#COPY --from=build /app/target/shorterUrl-0.0.1-SNAPSHOT.jar /app/app.jar
#
## Expose the port the app runs on
#EXPOSE 8080
#
## Specify default command
#CMD ["java", "-jar", "app.jar"]

#------------------------------------------------------------------------------------------
## Use an official Maven image with Eclipse Temurin JDK 17 as a parent image for building
#FROM maven:3.9.5-eclipse-temurin-17  AS build
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy only the Maven Wrapper files to a temporary directory
#COPY .mvn ./.mvn
#
## Copy the project files with a timestamp to avoid cache
#COPY . ./
#RUN echo "$(date)" > timestamp
#
## Set permissions for Maven Wrapper
#RUN chmod +x ./.mvn/wrapper/maven-wrapper.jar
#
## Build the project
#CMD ["./mvnw", "spring-boot:run"]
#
## Use an official Eclipse Temurin JDK 17 runtime as a parent image for the final stage
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy only the necessary files from the build stage
#COPY --from=build /app/target/shorterUrl-0.0.1-SNAPSHOT.jar /app/app.jar
#
## Expose the port the app runs on
#EXPOSE 8080
#
## Specify default command
#CMD ["java", "-jar", "app.jar"]


## Use an OpenJDK base image
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the JAR file into the container
#COPY target/shorterUrl-0.0.1-SNAPSHOT.jar /app/app.jar
#
#
## Expose the port the app runs on
#EXPOSE 8080
#
## Define any environment variables if needed
## ENV VARIABLE_NAME value
#
## Command to run your application
#CMD ["java", "-jar", "app.jar"]




FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /workspace/app

COPY mvnw .

COPY .mvn .mvn

COPY pom.xml .

COPY src src

RUN ./mvnw clean install -DskipTests

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

ARG DEPENDENCY=/workspace/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib

COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF

COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.shorterUrl.ShorterUrlApplication"]
