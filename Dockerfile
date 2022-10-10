# ---
# 1. Step: Build the jar
# ---

FROM gradle:jdk15 as builder
COPY --chown=gradle:gradle application /application
WORKDIR /application
RUN gradle clean build buildFatJar

# ---
# 2. Step: Run / start the server
# ---

# x86 machines
FROM openjdk:15

# Raspberry Pi
# FROM arm32v7/adoptopenjdk:15

EXPOSE 8080
COPY --from=builder /application/build/libs/fat.jar .
WORKDIR /
CMD java -jar ./fat.jar
 