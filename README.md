
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
        stage('Select docker registry') {
                 steps {
                      script {
                        def REGISTRY_LIST = []
                        def buildStages = env.getEnvironment()
                        for (builds in buildStages) {
                            if(builds.key.startsWith("REGISTRY_")) {
                              REGISTRY_LIST.add(builds.value)
                            }
                        }
                        env.select_docker_registry = input  message: 'Select docker registry ',ok : 'Proceed',id :'tag_id',
                        parameters:[choice(choices: REGISTRY_LIST, description: 'Select docker registry', name: 'dockerregistry')]
                        echo "Selected Registry is ${env.select_docker_registry}"
                        env.registryCredId="REGISTRY_"+env.select_docker_registry
                      }
                 }
              }
        stage('Select Environment') {
                 steps {
                      script {
                        def SERVER_LIST=[]
                        def buildStages = env.getEnvironment()
                        for (builds in buildStages) {
                            if(builds.key.startsWith("SERVER_")) {
                              SERVER_LIST.add(builds.value)
                            }
                        }
                        env.selected_environment = input  message: 'Select environment ',ok : 'Proceed',id :'tag_id',
                        parameters:[choice(choices: SERVER_LIST, description: 'Select environment', name: 'env')]
                        echo "Deploying ${env.selected_environment}."
                        env.credId= "SSH_"+env.selected_environment
                      }
                 }
        }

        stage('push Registry') {
          steps {
                    withCredentials([usernamePassword(credentialsId: env.registryCredId, passwordVariable: 'REGISTRY_PASSWORD', usernameVariable: 'REGISTRY_USERNAME')]) {
                        sh """
                              docker login -u ${REGISTRY_USERNAME} -p ${REGISTRY_PASSWORD} ${env.select_docker_registry}
                              docker tag microservice-jenkins-docker:latest ${env.select_docker_registry}/ritreshgirdhar/microservice-jenkins-docker:latest
                              docker push ${env.select_docker_registry}/ritreshgirdhar/microservice-jenkins-docker:latest
                              docker logout ${env.select_docker_registry}
                          """
                    }
          }
        }
        stage('deploy') {
          steps {
          withCredentials([
                usernamePassword(credentialsId: env.credId, passwordVariable: 'SSH_PASSWORD', usernameVariable: 'SSH_USERNAME'),
                usernamePassword(credentialsId: env.registryCredId, passwordVariable: 'REGISTRY_PASSWORD', usernameVariable: 'REGISTRY_USERNAME') ]) {
                sh """
                          cd ansible_deployment
                          ansible-playbook -i hosts playbooks/deploy.yaml
                          echo "[server]" > /tmp/hosts
                          echo " ${env.selected_environment}" >> /tmp/hosts
                          ansible-playbook -i /tmp/hosts playbooks/deploy.yaml  -e 'registry_username=${REGISTRY_USERNAME}' -e 'registry_password=${REGISTRY_PASSWORD}' -e "selected_server=${env.selected_environment}"  -e "selected_registry=${env.select_docker_registry}" -e "version=${env.version}" -e "ansible_ssh_pass=${SSH_PASSWORD}" -e "ansible_ssh_user=${SSH_USERNAME}""
                 """
              }
            }
        }
     }
   }
```

Here neither we are saving hardcoded details of any Docker registry's ip/port and credentials details nor we are saving hardcoded details of any server. We are configuring everything in Jenkins.

Steps to configure List of docker registries
* Login to Jenkins. Goto Manage Jenkins -> configuration Section
* Goto environment section and add environment variable with name REGISTRY_<IntValue>. IntValue represents lists of Registry servers.
* Now add list of Servers in same environment section. where you want to deploy the microservices. Use this format SERVER_<IntValue>
![Alt text](/images/environment-variables.png?raw=true "Environment Variables")
![Alt text](/images/registry-credentials.png?raw=true "Registry Credentials")
![Alt text](/images/Server1-credentials.png?raw=true "Server1 Credentials")

### Let's test this newly created Job.
![Alt text](/images/select_environment.png?raw=true "select environment")
![Alt text](/images/select_registry.png?raw=true "select registry")
![Alt text](/images/success.png?raw=true "successful deployment")
 
Happy learning :) 
