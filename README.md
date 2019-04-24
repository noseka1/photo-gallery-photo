# photo-gallery-photo

## Build

This component requires the `photo-gallery-common` library. Make sure you build that library first.

You can build this project using:

```
mvn clean install package
```

## Database

This component requires access to a PostgreSQL database. You can create it using:

```
psql -c 'CREATE DATABASE photodb'
psql -c "CREATE USER photouser WITH ENCRYPTED PASSWORD 'password'"
psql -c 'GRANT ALL PRIVILEGES ON DATABASE photodb TO photouser'
```

## Run

You can run this component as a standalone service using:

```
java -jar target/photo-gallery-photo-1.0-SNAPSHOT-runner.jar
```

After the service starts up you can test it using curl.

To create some photos:

```
curl -v -X POST -H 'Content-Type: application/json' --data '{"name":"Odie","category":"animals"}' localhost:8080/photos
curl -v -X POST -H 'Content-Type: application/json' --data '{"name":"Garfield","category":"animals"}' localhost:8080/photos
curl -v -X POST -H 'Content-Type: application/json' --data '{"name":"Empire state building","category":"buildings"}' localhost:8080/photos
```

To retrieve all created photos:

```
curl -v localhost:8080/photos
```

## Deploying to OpenShift

Create a new project if it doesn't exist:

```
oc new-project photo-gallery-distributed
```

Deploy a PostgreSQL database:

```
oc new-app \
--template postgresql-persistent \
--param DATABASE_SERVICE_NAME=postgresql-photo \
--param POSTGRESQL_USER=photouser \
--param POSTGRESQL_PASSWORD=password \
--param POSTGRESQL_DATABASE=photodb
```

Define a binary build (this will reuse the Java artifacts will built previously):

```
oc new-build \
--name photo \
--binary \
--strategy docker
```

Correct the Dockerfile location in the build config:

```
oc patch bc photo -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.jvm"}}}}'
```

Start the binary build:

```
oc start-build \
photo \
--from-dir . \
--follow
```

Deploy the application:

```
oc new-app \
--image-stream photo \
--name photo \
--env QUARKUS_DATASOURCE_URL=jdbc:postgresql://postgresql-photo:5432/photodb
```

Expose the application to the outside world:

```
oc expose svc photo
```
