# Use an official OpenJDK runtime as a parent image
FROM robsonoduarte/8-jre-alpine-bash:latest

# Set the working directory to /app
WORKDIR /app

# Create a log directory
RUN mkdir /log

# Copy the fat jar into the container at /app
COPY /target/coriander-ingress-api.jar /app

# Make port 80 available to the world outside this container
EXPOSE 80

# Run jar file when the container launches
CMD ["java", "-jar", "coriander-ingress-api.jar"]