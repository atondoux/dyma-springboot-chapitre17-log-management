FROM maven:3.9.11-eclipse-temurin-25 as build
COPY pom.xml .
COPY src/ src/
RUN mvn -f pom.xml -Pprod clean package

FROM eclipse-temurin:25-jre as run
RUN useradd dyma
USER dyma
COPY --from=build /target/dyma-tennis.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
