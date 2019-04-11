# photo-gallery-photo

Photo component

This component requires the `photo-gallery-common` library. Make sure you build that library first.

You can build this project using:

```
mvn clean install package
```

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
