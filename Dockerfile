FROM bellsoft/liberica-openjre-alpine:21.0.6-10
RUN apk update && apk upgrade && apk add --no-cache tini
WORKDIR /deployments
COPY target/nabu-*-SNAPSHOT.jar nabu.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD [ "/sbin/tini", "--", "java", "-jar", "nabu.jar" ]
