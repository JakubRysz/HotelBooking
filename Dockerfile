#
# Build stage
#
FROM gradle:7.6.0-jdk17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /target/shop-0.0.1-SNAPSHOT.jar shop.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","shop.jar"]