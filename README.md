# Nabu

* Example application.properties file

```
server.port=9004

# activemq settings
spring.activemq.broker-url=tcp://activemq:61616?jms.blobTransferPolicy.uploadUrl=http://activemq:8161/fileserver/
#spring.activemq.pooled=true
spring.activemq.user=admin
spring.activemq.password=admin

queue.gtfs.upload.destination.name=ExternalFileUploadQueue

# logging settings
logging.level.no.rutebanken=INFO
logging.level.org.apache=INFO
```

* Building
`mvn clean install`

* Building docker image
`mvn -Pf8-build`

* Testing with curl

`curl -vX POST -F "file=@pom.xml; filename=pom.xml" http://localhost:9004/opstatus/2/uploadFile`

* Running in docker (development)

`docker run -it --name nabu -e JAVA_OPTIONS="-Xmx1280m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.profiles.active=dev" --link activemq -p 5005:5005 -v /git/nabu_config/dev/application.properties:/app/config/application.properties:ro dr.rutebanken.org/rutebanken/nabu:0.0.1-SNAPSHOT`
