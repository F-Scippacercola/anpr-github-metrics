NAME = anpr-github-metrics/baseimage
VERSION = 1.0
CONTAINER_NAME = anpr-github-metrics
FRONTEND_CONTAINER_NAME = frontend-github-metrics
BASE_PATH_FRONTEND = $(shell pwd)/frontend

.PHONY: all build test tag_latest release ssh

all: build

build:
	echo Remember to configure "mvn" in your '$PATH' and '$JAVA_HOME'
	mvn -f backend/pom.xml clean install
	mkdir -p docker/jre && cp Dockerfile ./docker/. && cp -R ./backend/target/output/* docker/jre/.
	docker build -t $(NAME):$(VERSION) docker

start:
	-docker rm -f $(CONTAINER_NAME)
	docker run -p 19800:19800 --name $(CONTAINER_NAME) -d --restart always $(NAME):$(VERSION) 

start-frontend:
	-docker rm -f $(FRONTEND_CONTAINER_NAME)
	docker run --name $(FRONTEND_CONTAINER_NAME) --rm -v $(BASE_PATH_FRONTEND):/src -w /src -p 8080:8080 node:6 bash -c "npm install && npm run dev"

tag_latest:
	docker tag $(NAME):$(VERSION) $(NAME):latest

release: test tag_latest
	@if ! docker images $(NAME) | awk '{ print $$2 }' | grep -q -F $(VERSION); then echo "$(NAME) version $(VERSION) is not yet built. Please run 'make build'"; false; fi
	docker push $(NAME)
	@echo "*** Don't forget to create a tag by creating an official GitHub release."

test:
	mvn -f backend/pom.xml test
