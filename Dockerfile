FROM openjdk:11-jre
ADD target/nabu-*-SNAPSHOT.jar nabu.jar

EXPOSE 9004
CMD java $JAVA_OPTIONS -jar /nabu.jar