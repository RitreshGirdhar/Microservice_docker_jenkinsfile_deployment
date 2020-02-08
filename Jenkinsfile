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