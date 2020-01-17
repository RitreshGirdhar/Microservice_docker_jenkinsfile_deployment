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
              sh "pwd"
              sh "docker tag microservice1:latest 10.202.11.133:5000/microservice1:latest"
              sh "docker push 10.202.11.133:5000/microservice1:latest"
          }
        }
        stage('deploy') {
          steps {
            sh """
              cd ansible_deployment
              ansible-playbook -i hosts playbooks/deploy.yaml
              """
            }
        }
     }
   }
