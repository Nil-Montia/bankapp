FROM maven as maven
WORKDIR /build
COPY . .
RUN /usr/bin/mvn package
FROM java:8
WORKDIR /app
COPY --from=maven /build/target/Doit-0.5.jar app.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "app.jar"]