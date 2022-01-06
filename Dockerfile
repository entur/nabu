FROM adoptopenjdk/openjdk11:alpine-jre
RUN apk add --no-cache tini
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD [ "/sbin/tini", "--", "java", "-jar", "nabu.jar" ]
