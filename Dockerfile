#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/hotelBooking-0.0.1-SNAPSHOT.jar /app/hotelBooking.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/hotelBooking.jar"]