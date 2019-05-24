FROM maven as maven
WORKDIR /build
COPY . .
RUN /usr/bin/mvn package
FROM java:8
WORKDIR /app
COPY --from=maven /build/target/Banking-1.0.0.jar app.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "app.jar"]
