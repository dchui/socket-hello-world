# Carsome Technical Challenge

## About the Application
This is a Spring Boot application. To see it in action, the easiest way is to build it. You need [Maven](https://maven.apache.org/) and Java installed.


### Clusterability
This application can be clustered by:

* Configuring an external message broker (e.g. RabbitMQ). Due to time constraints, it is currently using an in-memory message broker for WebSockets.
* Configuring Quartz to use the database to manage its operations in a cluster. Quartz is used to send a message to clients to notify them a bid session has ended.
* Configuring an external database for data storage.

Note: Currently, the application is stateless. 

### Configuring the Application
You can configure the default duration a bid session lasts. To do that, find `src/main/resources/application.properties` and change the key `socketchallange.bid.session.default-length`. The value is in seconds.


## Running the Application

1. `mvn spring-boot:run` in the directory containing this README.
1. The application will seed its in-memory database with 100 dealers, 1 vehicle to be sold and a bid session.
1. Point your browser to `http://localhost:8080/index.html`.

## Using the Application
1. You join a bid session by specifying a dealer ID in the browser and then clicking `Connect`. 

   Currently, the valid dealer IDs range from 1-100 (see the section about running the application and data seeding). However, you can only join one specific bid session (102) at the moment. That's because the data seeding process creates only one bid session.
1. You can open up more than one tab to pretend to be different dealers. Just specify a different dealer ID and click `Connect`.

## Demo
https://streamable.com/rfce7