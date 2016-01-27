# Nabu

* Example application.properties file

```
# activemq settings
spring.activemq.broker-url=tcp://activemq:61616?jms.blobTransferPolicy.uploadUrl=http://localhost:8161/fileserver/
spring.activemq.user=admin
spring.activemq.password=your_password

queue.gtfs.upload.destination.name=gtfsQueue

# logging settings
logging.level.no.rutebanken=DEBUG
logging.level.org.apache=INFO
```

* Testing with curl
 
`curl -vX POST -F "file=@pom.xml; filename=pom.xml" -F "providerId=2" http://localhost:8080/opstatus/uploadFile`
