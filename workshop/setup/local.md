# Setup for local environment

Here are the instructions to run the workshop locally.

## Requirements

The workstation must have the following elements to be able to run the workshop locally:

- Strong internet access
- Java 21
- Docker using WSL 2 backend or with at least 4GB of memory allocated
- Availability of the following commands:
  - docker (or alternative as podman)
  - mvn

## Setup

These commands are to be executed once to initialize the Ollama instance container.

Create and start the Ollama instance

```shell
docker run --name ollama -p 11434:11434 ollama/ollama:latest
```

Create and start the Redis instance (for exercise 4 only)

```shell
docker run --name redis -p 6379:6379 redis/redis-stack:latest
```

## Run the workshop

These commands can be executed as many times as necessary to complete the workshop steps.

(Optional) Restart the Ollama instance

```shell
docker start ollama
```

(Optional) Restart the Redis instance (for exercise 4 only)

```shell
docker start redis
```

Build the application

```shell
mvn -s .mvn/settings.xml clean install
```

Run the application

```shell
mvn -s .mvn/settings.xml spring-boot:run
```

[Go back](../../README.md)
