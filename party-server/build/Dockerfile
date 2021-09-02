FROM openjdk:8-jre-alpine

WORKDIR /app

COPY . /app

EXPOSE 21360

# Setup timezone
RUN apk add --no-cache tzdata
ENV TZ Europe/Berlin

# Setup locale
ENV LANG de_DE.UTF-8
ENV LANGUAGE de_DE.UTF-8
ENV LC_ALL de_DE.UTF-8

CMD ["java", "-jar", "party-1.0-SNAPSHOT.jar", "server", "config.yml"]
