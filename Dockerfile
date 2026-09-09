FROM bellsoft/liberica-openjre-alpine:25.0.4.1 AS builder
WORKDIR /builder
COPY target/*-SNAPSHOT.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM gcr.io/distroless/java25-debian13:nonroot@sha256:fce4a1d66284e8866c46113d9bdc286c46fb8c3c3f0a098f877034349e88debe
WORKDIR /deployments
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
ENTRYPOINT [ "/usr/bin/java", "-jar", "application.jar" ]
