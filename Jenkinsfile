
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    environment {
        DOCKER_IMAGE = 'lyfzfun/finsecure-pay'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out FinSecure Pay source code'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the Spring Boot application'
                bat 'mvnw.cmd clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running application tests'
                bat 'mvnw.cmd test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application'
                bat 'mvnw.cmd package -DskipTests'
            }
        }

        stage('Security Checks') {
            steps {
                echo 'Security scanning will be configured in Task 5'
            }
        }

        stage('Container Build') {
            steps {
                echo 'Building the FinSecure Pay Docker image'
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds-new',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    bat 'powershell.exe -NoProfile -NonInteractive -ExecutionPolicy Bypass -File .\\docker-push.ps1'
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                bat '''
                    kubectl set image deployment/finsecure-pay ^
                        finsecure-pay=%DOCKER_IMAGE%:%BUILD_NUMBER%
                    if errorlevel 1 exit /b 1

                    kubectl rollout status deployment/finsecure-pay --timeout=180s
                    if errorlevel 1 exit /b 1
                '''
            }
        }
    }

    post {
        success {
            echo 'FinSecure Pay pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }

        always {
            echo 'Pipeline execution finished.'
        }
    }
}
