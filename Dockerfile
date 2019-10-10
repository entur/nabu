FROM openjdk:11-jre
WORKDIR /deployments
ADD target/nabu-*-SNAPSHOT.jar nabu.jar
CMD java $JAVA_OPTIONS -jar /nabu.jar
