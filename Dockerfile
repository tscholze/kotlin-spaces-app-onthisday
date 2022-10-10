# ---
# 1. Step: Build the jar
# ---

FROM gradle:jdk15 as builder
COPY --chown=gradle:gradle application /application
WORKDIR /application
RUN gradle clean build jar

# ---
# 2. Step: Run / start the server
# ---

# x86 machines
FROM openjdk:15

# Raspberry Pi
# FROM arm32v7/adoptopenjdk:15

EXPOSE 8080
COPY --from=builder /application/build/libs/miniktor-0.0.1.jar .
WORKDIR /
CMD java -jar ./miniktor-0.0.1.jar
 