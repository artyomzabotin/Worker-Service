# Worker Service

## Description
Worker service - is a microservice that is created to manage with Worker entity. In future there will be all CRUD operations. 
Now there are some REST endpoints to retrieve users and to report time to [Scheduler Service](https://github.com/artyomzabotin/Schedule-Service).

## Getting started
Copy and start these applications: 

[Worker Service](https://github.com/artyomzabotin/Worker-Service)

[Scheduler Service](https://github.com/artyomzabotin/Schedule-Service)

[Service Registry](https://github.com/artyomzabotin/Service-Registry)

Install locally [Kafka](https://kafka.apache.org/downloads) and start Zookeeper and Kafka servers. Then you need to run Redis and Redis Commander using Docker. Go to root of Scheduler Service repo and type:

````
docker-compose -f docker-compose.yml -up
````

Now you are ready to use application.

