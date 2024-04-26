FROM eclipse-temurin:21-jdk-alpine

# Postgres
ENV PG_HOST=''
ENV PG_PORT=''
ENV PG_DB=''
ENV PG_USER=''
ENV PG_PASS=''
# API Prefix
ENV API_PREFIX=''

#Change name
COPY ./build/libs/yda.jar /app.jar

# Install required packages for downloading and unzipping
RUN apk --no-cache add curl unzip

# Start the Java application with the Glowroot agent
ENTRYPOINT ["java","-jar","app.jar"]
