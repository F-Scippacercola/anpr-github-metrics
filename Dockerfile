FROM java:8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
#ENV GITLAB_OAUTH_TOKEN _YOUR_OAUTH_TOKEN_

RUN mkdir -p /task/bin /task/jre
ADD jre/. /task/jre/

RUN chmod 777 /task/jre/microservice.jar

EXPOSE 19800
WORKDIR /task/jre/
ENTRYPOINT ["/bin/bash", "-c", "java -jar microservice.jar"]
