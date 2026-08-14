FROM gradle:8.11-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 10000
CMD java -jar app.jar -port=${PORT:-10000}