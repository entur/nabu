# Nabu

## Example application.properties file for development

```
server.port=9004

# activemq settings
spring.activemq.broker-url=tcp://activemq:61616?jms.blobTransferPolicy.uploadUrl=http://activemq:8161/fileserver/
#spring.activemq.pooled=true
spring.activemq.user=admin
spring.activemq.password=admin
spring.jms.pub-sub-domain=true

# marduk file upload queue
queue.gtfs.upload.destination.name=ExternalFileUploadQueue

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
spring.activemq.broker-url=tcp://activemq:61616?jms.blobTransferPolicy.uploadUrl=http://activemq:8161/fileserver/
#spring.activemq.pooled=true
spring.activemq.user=admin
spring.activemq.password=admin
spring.jms.pub-sub-domain=true

# marduk file upload queue
queue.gtfs.upload.destination.name=ExternalFileUploadQueue

# logging settings
logging.level.org.hibernate.tool.hbm2ddl=INFO
logging.level.org.hibernate.SQL=INFO
logging.level.org.hibernate.type=WARN
logging.level.org.springframework.orm.hibernate4.support=WARN
logging.level.no.rutebanken=INFO
logging.level.org.apache=INFO

# JPA settings (in-memory) 
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create

# JPA settings (postgres)
# spring.jpa.database=POSTGRESQL
# spring.datasource.platform=postgres
# spring.jpa.show-sql=true
# spring.jpa.hibernate.ddl-auto=none
# spring.database.driverClassName=org.postgresql.Driver
# spring.datasource.url=jdbc:postgresql://localhost:5532/postgres

spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.datasource.initializationFailFast=false

```

## Build and Run

* Building
`mvn clean install`

* Building docker image
`mvn -Pf8-build`

* Testing with curl
`curl -vX POST -F "file=@pom.xml; filename=pom.xml" http://localhost:9004/opstatus/2/uploadFile`

* Running in docker (development)
`docker rm -f nabu ; docker run -it --name nabu -e JAVA_OPTIONS="-Xmx1280m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" --link activemq -p 5005:5005 -v /git/config/nabu/dev/application.properties:/app/config/application.properties:ro dr.rutebanken.org/rutebanken/nabu:0.0.1-SNAPSHOT`

* Running in docker (test ++)
`docker run -it --name nabu -e JAVA_OPTIONS="-Xmx1280m" --link activemq --link some-postgres -v /git/config/nabu/test/application.properties:/app/config/application.properties:ro dr.rutebanken.org/rutebanken/nabu:0.0.1-SNAPSHOT`
