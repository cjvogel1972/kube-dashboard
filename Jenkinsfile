pipeline {
    agent any
    tools {
        jdk "JDK9"
        gradle "Gradle5.4"
    }
    stages {
        stage('run sonar') {
            steps {
                 withSonarQubeEnv {
                     sh 'gradle sonarqube'
                 }
           }
        }
    }
}