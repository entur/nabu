FROM eclipse-temurin:17.0.4_8-jre-alpine
RUN apk update && apk upgrade && apk add --no-cache tini
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD [ "/sbin/tini", "--", "java", "-jar", "nabu.jar" ]
