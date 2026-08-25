FROM eclipse-temurin:8-jre

WORKDIR /app
COPY tfcloud-eureka-server-1.2.1.jar /app/app.jar

EXPOSE 20000

ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-Djava.awt.headless=true", "-Djava.net.preferIPv4Stack=true", "-jar", "/app/app.jar", "--spring.profiles.active=common,local,running"]

