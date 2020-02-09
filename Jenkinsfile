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