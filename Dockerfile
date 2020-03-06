FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
CMD java $JAVA_OPTIONS -jar nabu.jar
