FROM openjdk:11-jre
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
CMD java $JAVA_OPTIONS -jar nabu.jar
