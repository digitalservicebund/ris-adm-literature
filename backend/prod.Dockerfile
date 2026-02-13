FROM gradle:jdk25 AS builder

WORKDIR /app

# Copy for caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build project but skip tests and checks
RUN gradle clean bootJar -x test -x spotlessCheck -x checkstyleMain

# Start backend app with image: cgr.dev/chainguard/jre:25
FROM cgr.dev/chainguard/jre@sha256:5579ed9959a5454fcdfcd2ac5c3c27321d7ecf4dc94f820c3d81bb36488f66b3

WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/build/libs/ris-adm-literature-backend-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
