FROM maven:3.8.6-eclipse-temurin-11 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM payara/micro:5.2022.5

COPY --from=build /app/target/foodcart.war /opt/payara/deployments/foodcart.war

CMD ["--port", "10000"]