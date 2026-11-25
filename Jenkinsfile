pipeline {
    agent {
        label 'windows-java21-agent'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Получение исходного кода из Git'
                checkout scm
            }
        }

        stage('Toolchain') {
            steps {
                echo 'Проверка Java'
                bat 'java -version'
                echo 'Проверка Maven'
                bat 'mvn -version'
                echo 'Проверка Git'
                bat 'git --version'
            }
        }

        stage('Build and Verify') {
            steps {
                echo 'Запуск полной проверки проекта'
                bat 'mvn -B -ntp clean verify'
            }
        }

        stage('Generate Reports') {
            steps {
                echo 'Формирование HTML-отчёта Surefire'
                bat 'mvn -B -ntp surefire-report:report-only'
                echo 'Формирование HTML-отчёта JaCoCo'
                bat 'mvn -B -ntp jacoco:report'
            }
        }
    }

post {
    always {
        junit(
            allowEmptyResults: true,
            testResults: 'target/surefire-reports/TEST-*.xml,target/failsafe-reports/TEST-*.xml'
        )

        archiveArtifacts(
            artifacts: 'target/surefire-reports/**,target/failsafe-reports/**,target/site/**',
            allowEmptyArchive: true
        )
    }

    success {
        archiveArtifacts(
            artifacts: 'target/task-manager-0.0.1-SNAPSHOT.jar',
            fingerprint: true
        )

        withCredentials([
            string(credentialsId: 'telegram-bot-token', variable: 'TG_TOKEN'),
            string(credentialsId: 'telegram-chat-id', variable: 'TG_CHAT_ID')
        ]) {
            powershell '''
                $msg = "Jenkins: сборка task-manager завершилась успешно. Build #$env:BUILD_NUMBER"
                $url = "https://api.telegram.org/bot$env:TG_TOKEN/sendMessage"
                $body = @{
                    chat_id = $env:TG_CHAT_ID
                    text    = $msg
                } | ConvertTo-Json

                Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json" -Body $body
            '''
        }

        echo 'Pipeline выполнен успешно.'
    }

    failure {
        withCredentials([
            string(credentialsId: 'telegram-bot-token', variable: 'TG_TOKEN'),
            string(credentialsId: 'telegram-chat-id', variable: 'TG_CHAT_ID')
        ]) {
            powershell '''
                $msg = "Jenkins: сборка task-manager завершилась ошибкой. Build #$env:BUILD_NUMBER"
                $url = "https://api.telegram.org/bot$env:TG_TOKEN/sendMessage"
                $body = @{
                    chat_id = $env:TG_CHAT_ID
                    text    = $msg
                } | ConvertTo-Json

                Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json" -Body $body
            '''
        }

        echo 'Pipeline завершился с ошибкой.'
        echo 'Необходимо изучить Console Output и отчёты.'
    }

    unstable {
        withCredentials([
            string(credentialsId: 'telegram-bot-token', variable: 'TG_TOKEN'),
            string(credentialsId: 'telegram-chat-id', variable: 'TG_CHAT_ID')
        ]) {
            powershell '''
                $msg = "Jenkins: сборка task-manager нестабильна. Build #$env:BUILD_NUMBER"
                $url = "https://api.telegram.org/bot$env:TG_TOKEN/sendMessage"
                $body = @{
                    chat_id = $env:TG_CHAT_ID
                    text    = $msg
                } | ConvertTo-Json

                Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json" -Body $body
            '''
        }

        echo 'Pipeline имеет статус UNSTABLE.'
        echo 'Необходимо проверить результаты тестирования.'
    }
}
}