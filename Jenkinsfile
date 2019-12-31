def STAGES=["Code Checkout","Code Compile"]
if (env.UnitTesting == "true") {
           STAGES.add("Unit Testing")
}
         if (env.CodeAnalysis == "true") {
           STAGES.add("SonarQube Analysis")
         }
         if (env.QualityGates == "true") {
           STAGES.add("Quality Gates")
         }
         if (env.DeployCustomApi == "true") {
           STAGES.add("CustomAPI Deploy")
         }
         if (env.Notification == "true") {
           STAGES.add("Notification")
         }

pipeline {
    agent { docker { image 'maven:3.3.3' } }
    stages {
        stage("checkout"){
                     def g = new git()
                          g.Checkout("${config.GIT_URL}","${env.Branch}","${config.GIT_CREDENTIALS}")
        }
        stage('build') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}