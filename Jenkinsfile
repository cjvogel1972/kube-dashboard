pipeline {
    tools {
        jdk "JDK9"
        gradle "Gradle5.4"
    }
    stages {
        stage {
            steps {
                 withSonarQubeEnv {
                     sh 'gradle sonarqube'
                 }
           }
        }
    }
}