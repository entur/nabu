# Nabu

## Liveness and readyiness
In production, nabu can be probed with:
- http://localhost:9004/jersey/appstatus/up
- http://localhost:9004/jersey/appstatus/ready
to check liveness and readiness, accordingly

## Example application.properties file for development

```
server.port=9004

# activemq settings
spring.activemq.broker-url=tcp://activemq:61616
#spring.activemq.pooled=true
spring.activemq.user=admin
spring.activemq.password=admin
spring.jms.pub-sub-domain=true

# marduk file upload queue
queue.upload.destination.name=MardukInboundQueue

# blob store config
blobstore.gcs.project.id=carbon-1287
blobstore.gcs.container.name=marduk-exchange
blobstore.gcs.credential.path=/home/tomgag/.ssh/Carbon-a4d50ca8176c.json

# JPA settings (in-memory)
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create

# logging settings
logging.level.no.rutebanken=INFO
logging.level.org.apache=INFO
```

## Example application.properties file for test

```

server.port=9006

# activemq settings
spring.activemq.broker-url=tcp://activemq:61616
#spring.activemq.pooled=true
spring.activemq.user=admin
spring.activemq.password=admin

# marduk file upload queue
queue.upload.destination.name=MardukInboundQueue

# logging settings
logging.level.org.hibernate.tool.hbm2ddl=INFO
logging.level.org.hibernate.SQL=INFO
logging.level.org.hibernate.type=WARN
logging.level.org.springframework.orm.hibernate4.support=WARN
logging.level.no.rutebanken=INFO
logging.level.org.apache=WARN

# blobstore settings
blobstore.gcs.project.id=carbon-1287
blobstore.gcs.container.name=marduk-exchange
blobstore.gcs.credential.path=/home/tomgag/.ssh/Carbon-a4d50ca8176c.json

# JPA settings (postgres)
spring.jpa.database=POSTGRESQL
spring.datasource.platform=postgres
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=update
spring.database.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://nabudb:5432/nabu
spring.datasource.username=nabu
spring.datasource.password=topsecret

spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.datasource.initializationFailFast=false

```

## Build and Run

* Building
`mvn clean install`

* Building docker image (using profile h2 for in-memory DB)
`mvn -Pf8-build,h2`

* Running
`mvn spring-boot:run -Ph2 -Dspring.config.location=/path/to/application.properties`

* Testing with curl
`curl -vX POST -F "file=@\"avinor-netex_201609291122.zip\"" http://localhost:9004/jersey/files/21`

* Running in docker (development)
`docker rm -f nabu ; docker run -it --name nabu -e JAVA_OPTIONS="-Xmx1280m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" --link activemq -p 5005:5005 -v /git/config/nabu/dev/application.properties:/app/config/application.properties:ro dr.rutebanken.org/rutebanken/nabu:0.0.1-SNAPSHOT`

* Running in docker (test ++)
`docker run -it --name nabu -e JAVA_OPTIONS="-Xmx1280m" --link activemq --link some-postgres -v /git/config/nabu/test/application.properties:/app/config/application.properties:ro dr.rutebanken.org/rutebanken/nabu:0.0.1-SNAPSHOT`
