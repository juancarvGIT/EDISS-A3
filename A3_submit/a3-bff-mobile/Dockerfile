FROM eclipse-temurin:17-jdk-alpine

#Label

EXPOSE 80

ENTRYPOINT java ${JVM_ARGS:- -Xms128m} -jar app.jar

WORKDIR /opt/app

#Security
ARG USER=appUser
ARG UID=1000

RUN adduser --disabled-password --gecos $USER --uid $UID $USER
ADD target/a3-bff-mobile-0.0.1-SNAPSHOT.jar app.jar
RUN chown -R $USER .
COPY ./target/a3-bff-mobile-0.0.1-SNAPSHOT.jar .
USER $USER