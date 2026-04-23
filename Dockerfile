FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} imporived-billing.jar
ENTRYPOINT ["java","-jar","/imporived-billing.jar"]
EXPOSE 8080
