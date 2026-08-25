FROM eclipse-temurin:8-jre

ARG JAR_FILE

WORKDIR /app
COPY ${JAR_FILE} /app/app.jar

RUN mkdir -p /app/runtime && chown -R 1000:1000 /app/runtime

WORKDIR /app/runtime

USER 1000:1000

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
