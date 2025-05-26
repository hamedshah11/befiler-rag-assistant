# ---------- build stage ----------
FROM gradle:8-jdk17 AS build
WORKDIR /workspace/app
COPY --chown=gradle:gradle . .
RUN gradle bootJar

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
