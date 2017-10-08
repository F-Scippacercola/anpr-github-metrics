# Anpr-github-metrics

A microserver and a client-side application that computes and shows metrics of Issue Trackers.

# Description

Anpr-github-metrics is a **JS client-side application** that shows metrics on Issue Trackers, using a **processing microservice** in the backed.
- **GUI Extensibility** The JS application is based on *Vue* and is easily extensible for adding new plots and reports to the users.
- **Virtualized environment** The application communicates with a backend microservice (*Dockerized on-the-go*), to guarantee a stable and easy-to-set environment.
- **Standard communication** The communication uses a standard *REST interface* based on *Swagger* ([See Swagger YAML Specification](https://github.com/F-Scippacercola/anpr-github-metrics/swagger-api.yaml)), to allow the system maintanability.
- **Powerful frameworks** The microservice runs in a **Java SE Virtual Machine** (it is not required a Java EE!) and exploites the services of **Spring** and **Spring Boot**.
- **Maintainability and Extensibility** The microservice adopts a **compile-time plugin architecture**:
    1. Currently implements analysis on *GitHub*, but can be extended to other issue tracker (e.g. BugZilla) in moments!.
    2. New plugins for computing new metrics can be easily added at compile-time and registered to the microservice.
- **Designed with performance in mind** The microservice uses a **high-concurrent local time-cache** (by using **Google Guava _Cache_ and _Striped Locks_**) to improve the LRU queries. _The caching is completely transparent to the plugins!_ ([See how transparent caching has been implemented](https://github.com/F-Scippacercola/anpr-github-metrics/systems/fervento/gitlabissueanalyzer/issuefetcher/CachedIssueFetcher.java))
- **Testability by design** The application exploits strategies and design pattern to improve testability (unit and integration) preferring **Inversion Of Control**, **Builders**, **Fluent APIs**, and :_over all_: **Readable code!**.

## Currently Implemented Plugins

The plugin architecture allows to easily add new Issue Trackers and Metrics.
- Currentely implemented Issue Tracker:
    * GitHub
- Currently implemented Metric Plugins:
    1. **TicketGeneral** Computes the total numbers of open and closed tickets of a repository.
    2. **IssuesClosedWithoutComments** Retrieves all issues closed without comments.
    3. **TicketClosingTime** Finds the average time of a ticket resolution, providing the **time resolution histogram**.
    4. **FirstReplyTime** Computes the average time of a first comment to a new issue, providing the **time histogram**.
    5. **IssuesCommentedBy** Finds all the issues (open/closed/all) commented (or not commented) by a set of users.
    5. **IssuesWithLabels** Finds all the issues (open/closed/all) with (or without) a set of labels.

### Prerequisites

You need to have installed on your computer:
    1. Docker
    2. Build-tools (maven/make)

### Installing

Download the project, open the console and _cd_ into the project folder. Then, follow these steps:
    1. [Optional] decomment (removing #) and set your `GITLAB_OAUTH_TOKEN` in the Dockerfile
    2. Compile, build microservice, and start docker instance with the command:

```
    make build start
```
3. To run the frontend in the same host run also:
```
    make start-frontend
```

_Goto_ http://localhost:8080 and you are on the go! ;-)

## Running the tests

To run the test just call

```
    make test
```

## Deployment

By default the service runs on docker on your local machine. Modify the `Makefile` to change the environment.

## Built With

* [Swagger](https://swagger.io/) - The world's most popular API Tooling
* [Docker](https://www.docker.com/) - Docker is the worldâs leading software container platform
* [Spring](https://projects.spring.io/spring-framework/) - Spring Framework
* [Google Guava](https://github.com/google/guava) - Google Core Libraries for Java
* [Maven](https://maven.apache.org/) - Dependency Management
* [Vue](https://vuejs.org/) - The Progressive JavaScript Framework
* [Bootstrap 4](https://v4-alpha.getbootstrap.com/) - Bootstrap The most popular HTML, CSS, and JS framework

## Authors

**Ermanno Battista** (ermanno.battista@gmail.com) and **Fabio Scippacercola** (f.scippacercola@gmail.com) - Software Engineers and PhDs. Working in [IVM Tech](http://ivmtech.it/) and starting  [Fervento.systems](http://fervento.systems).

## Acknowledgments

* This code was developed in the 28-hour of the [Developers Italia Hackathon](https://developers.italia.it) held on the 7-8th October, 2017.
