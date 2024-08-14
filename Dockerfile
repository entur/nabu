FROM bellsoft/liberica-openjdk-alpine:21.0.4-9
RUN apk update && apk upgrade && apk add --no-cache tini
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD [ "/sbin/tini", "--", "java", "-jar", "nabu.jar" ]
