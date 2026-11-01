pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
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

        stage('Docker Build') {
            steps {
                echo 'Building the FinSecure Pay Docker image'
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    bat '''
                        echo %DOCKERHUB_PASSWORD% | docker login --username %DOCKERHUB_USER% --password-stdin
                        if errorlevel 1 exit /b 1
                        docker push %DOCKER_IMAGE%:%BUILD_NUMBER%
                        if errorlevel 1 exit /b 1
                        
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                bat '''
                    kubectl set image deployment/finsecure-pay ^
                        finsecure-pay=%DOCKER_IMAGE%:%BUILD_NUMBER%
                    kubectl rollout status deployment/finsecure-pay --timeout=180s
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
