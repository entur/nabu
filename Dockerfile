FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD java $JAVA_OPTIONS -jar nabu.jar
