FROM openjdk:17-oracle  
ARG JAR_FILE=target/java-event-telegram-bot-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} event-app.jar
ENTRYPOINT ["java","-jar","event-app.jar"]
ADD SSL/* /opt/app/