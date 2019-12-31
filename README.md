
# WIP Docker based Microservice deployment via jenkinsfile
Docker based Microservice - push to registry via Jenkins tool by defining steps in jenkinsfile

####Pre-requisite 
* You should have docker installed on your machine.

```$xslt
cd microservice-with-jenkins-docker
mvn clean install
```

Check docker images by running below command
```$xslt
$ docker images
REPOSITORY                                                TAG                 IMAGE ID            CREATED              SIZE
microservice-jenkins-docker/microservice-jenkins-docker   latest              541e5d2424f5        About a minute ago   124MB
```


