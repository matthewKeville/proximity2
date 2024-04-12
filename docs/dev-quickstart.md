# database setup


```sql
  CREATE DATABASE proximity;
  CREATE USER 'proximitydev'@'localhost' IDENTIFIED BY '33myopicfish';
  GRANT ALL PRIVILEGES ON *.* TO 'proximitydev'@localhost IDENTIFIED BY '33myopicfish';
```

login credentials and database name must be consistent with data described in
[application-local.properties](src/main/resources/application-local.properties)


# local secrets


Private information like api keys should be stored in the secret properties file
To use Eventbrite scanner, we need an api key. This should be set in
[application-secret.properties](src/main/resources/application-secret.properties)

```config
# Eventbrite api key goes here
proximity.Eventbrite.api.key=<your-api-key-here>
```

!!! Do not commit this file to the repository !!!


# creating template date


```sh
mvn spring-boot:run -Dspring-boot.run.arguments="--create-dev-data=true" 
```
this create some sample regions and test accounts to debug against.


# install node modules

```sh
npm i && npm i -D
```


# compile js bundle (and recompile on change)


```sh
npm run-script watch
```


# running locally

```sh
mvn spring-boot:run
```

this starts tomcat on at localhost:8080


