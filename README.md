# Nabu [![CircleCI](https://circleci.com/gh/entur/nabu/tree/master.svg?style=svg)](https://circleci.com/gh/entur/nabu/tree/master)

## Liveness and readyiness
In production, nabu can be probed with:
- http://localhost:9004/jersey/appstatus/up
- http://localhost:9004/jersey/appstatus/ready
to check liveness and readiness, accordingly

## Example application.properties file for development

```
server.port=9004

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

# logging settings
logging.level.org.hibernate.tool.hbm2ddl=INFO
logging.level.org.hibernate.SQL=INFO
logging.level.org.hibernate.type=WARN
logging.level.org.springframework.orm.hibernate4.support=WARN
logging.level.no.rutebanken=INFO
logging.level.org.apache=WARN

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

* Building docker image (distroless runtime, see `Dockerfile`)
`mvn verify && docker build -t nabu:latest .`

* Running
`mvn spring-boot:run -Ph2 -Dspring.config.location=/path/to/application.properties`

* Testing with curl
`curl -vX POST -F "file=@\"avinor-netex_201609291122.zip\"" http://localhost:9004/jersey/files/21`

* Running in docker (development)
`docker run -it --rm --name nabu -e JDK_JAVA_OPTIONS="-Xmx1280m -Dspring.config.location=/etc/application-config/application.properties -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" -e TZ=Europe/Oslo -p 9004:9004 -p 5005:5005 -v /path/to/application.properties:/etc/application-config/application.properties:ro nabu:latest`

The runtime image is distroless: no shell or package manager. For a shell, build with the `debug-nonroot` tag of the same base image.


# Flyway
To create the database for nabu, download and use the flyway command line tool:
https://flywaydb.org/documentation/commandline/

## Migration
Execute the migration. Point to the migration files in nabu.

```
./flyway -url=jdbc:postgresql://localhost:5433/nabu -locations=filesystem:/path/to/nabu/src/main/resources/db/migration migrate
```

### Example migration
```
./flyway -url=jdbc:postgresql://localhost:5433/nabu -locations=filesystem:/path/to/nabu/src/main/resources/db/migration migrate
Flyway 4.2.0 by Boxfuse

Database password: 
Database: jdbc:postgresql://localhost:5433/nabu (PostgreSQL 9.6)
Successfully validated 1 migration (execution time 00:00.016s)
Creating Metadata table: "public"."schema_version"
Current version of schema "public": << Empty Schema >>
Migrating schema "public" to version 1 - Base version
Successfully applied 1 migration to schema "public" (execution time 00:04.220s).
```


## Baseline existing database
To baseline an existing database that does not contain the table `schema_version`.
The schema of this database must be exactly equivalent to the first migration file. If not, you might be better off by starting from scratch and using the restoring_import to repopulate the new database.

```
./flyway -url=jdbc:postgresql://localhost:6432/nabu -locations=filesystem:/path/to/nabu/src/main/resources/db/migration baseline
```


## Schema changes
Create a new file according to the flyway documentation in the folder `resources/db/migration`.
Commit the migration together with code changes that requires this schema change.
