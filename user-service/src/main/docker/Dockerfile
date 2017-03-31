FROM anapsix/alpine-java:8
VOLUME /tmp
ADD user-service-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENV JPDA_ADDRESS="9191"
ENV JPDA_TRANSPORT="dt_socket"
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9191","-jar","/app.jar"]