# photo-gallery-photo

Management of photos

This component requires `photo-gallery-common` library.

 You can build this project using:

```
mvn clean install package
```

You can run this component as a standalone service using:

```
java -jar ./target/photo-gallery-photo-1.0-SNAPSHOT-fat.jar
```

After the service starts up you can test it using curl.

You can create some photos:

```
curl -v -X POST --data '{"name":"Odie","category":"animals"}' localhost:8080/photos
curl -v -X POST --data '{"name":"Garfield","category":"animals"}' localhost:8080/photos
curl -v -X POST --data '{"name":"Empire state building","category":"buildings"}' localhost:8080/photos
```

You can retrieve all created photos:

```
curl -v localhost:8080/photos
```
