pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Environment') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn -B -ntp clean verify'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
            }
        }

        stage('Publish JaCoCo Coverage') {
            steps {
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    inclusionPattern: '**/*.class',
                    exclusionPattern: ''
                )
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build completed successfully.'
        }
        failure {
            echo 'FAILURE: Build failed.'
        }
        always {
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', fingerprint: true, allowEmptyArchive: true
        }
    }
}