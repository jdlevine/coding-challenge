# coding-challenge

To build and run the docker image:

$ docker build -t springio/gs-spring-boot-docker .

$ docker run -p 8080:8080 springio/gs-spring-boot-docker


Alternatively, you can run the server with java:

$ java -jar target\interview-0.0.1-SNAPSHOT.jar
