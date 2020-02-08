
# Docker based Microservice deployment via jenkinsfile
Docker based Microservice - push to registry via Jenkins tool by defining steps in jenkinsfile

#### Pre-requisite 
* You should have docker installed on your machine.
* Do set up jenkins on your local or server via docker as defined here https://github.com/RitreshGirdhar/jenkins-with-docker 
* should have basic knowledge of docker
* should have basic knowledge of ansible
* should have basic knowledge of docker-compose


Let's Checkout the code.
```$xslt
mvn clean install 
```

Post build success , you will be able to see generated docker image by using below commands.
```$xslt
$ docker images
REPOSITORY                                                TAG                 IMAGE ID            CREATED              SIZE
microservice-jenkins-docker   latest              541e5d2424f5        About a minute ago   124MB
```

### Jenkins Deployment Pipeline options
* Declarative Pipelines
* Scripted Pipelines

Here we are follow Declarative Pipelines

#### Declarative Pipelines
* Declarative Pipeline is a more recent feature of Jenkins Pipeline
* provides richer syntactical features over Scripted Pipeline syntax, and
* Is designed to make writing and reading Pipeline code easier.
* Many of the individual syntactical components (or "steps") written into a Jenkinsfile.

### Let's understand the JenkinsFile 

```$xslt
pipeline {
   agent any
   stages {
        stage('Checkout') {
          steps {
            git branch: "master", url:'https://github.com/RitreshGirdhar/Microservice_docker_jenkinsfile_deployment.git'
          }
        }
        stage('Build') {
            steps {
                sh "mvn clean install"
            }
        }
        stage('push Registry') {
          steps {
              sh "docker tag microservice-jenkins-docker:latest 10.202.11.133:5000/microservice-jenkins-docker:latest"
              sh "docker push 10.202.11.133:5000/microservice-jenkins-docker:latest"
          }
        }
        stage('deploy') {
          steps {
            sh """
              cd ansible_deployment
              ansible-playbook -i hosts playbooks/deploy.yaml
              echo "[server]" > /tmp/hosts
              echo " ${env.selected_environment}" >> /tmp/hosts
              ansible-playbook -i /tmp/hosts playbooks/deploy.yaml -e "selected_server=${env.selected_environment}" -e 'repository_name=${env.REGISTRY_REPOSITORY}'  -e "selected_registry=${env.select_docker_registry}" -e "version=${env.version}" --extra-vars 'ansible_ssh_pass=${SERVER_PASSWORD}' --extra-vars='ansible_ssh_user=${env.SERVER_USER_DETAILS}'
              """
            }
        }
     }
   }
```

Here neither we are saving hardcoded details of any Docker registry's ip/port and credentials details nor we are saving hardcoded details of any server. We are configuring everything in Jenkins.

Steps to configure List of docker registries
* Login to Jenkins. Goto Manage Jenkins -> configuration Section
* Goto environment section and add environment variable with name REGISTRY_<numericvalue>
* 






Once jenkins is up.

Happy learning :) 
