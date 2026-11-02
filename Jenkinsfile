
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

        stage('Static Code Analysis') {
            steps {
                echo 'Running SpotBugs and FindSecBugs'
                bat 'mvnw.cmd spotbugs:spotbugs spotbugs:check'
            }
        }

        stage('Dependency Security Scan') {
            steps {
                echo 'Scanning Maven dependencies with Trivy'

                bat '''
                    trivy fs ^
                        --scanners vuln ^
                        --severity HIGH,CRITICAL ^
                        --exit-code 1 ^
                        --format json ^
                        --output trivy-dependency-report.json ^
                        .
                '''
            }
        }
        stage('Package') {
            steps {
                echo 'Packaging the application'
                bat 'mvnw.cmd package -DskipTests'
            }
        }

        stage('Container Build') {
            steps {
                echo 'Building the FinSecure Pay Docker image'

                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% .'
            }
        }

        stage('Container Image Security Scan') {
            steps {
                echo 'Scanning Docker image with Trivy'

                bat '''
                    trivy image ^
                        --severity HIGH,CRITICAL ^
                        --exit-code 1 ^
                        --format json ^
                        --output trivy-image-report.json ^
                        %DOCKER_IMAGE%:%BUILD_NUMBER%
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing verified Docker image to Docker Hub'

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
                echo 'Deploying verified image to Amazon EKS'

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
            echo 'All configured security checks passed.'
        }

        failure {
            echo 'Pipeline failed. Check the failed stage and security reports.'
        }

        always {
            echo 'Archiving security scan reports.'

            archiveArtifacts(
                artifacts: 'trivy-*-report.json,target/spotbugsXml.xml',
                allowEmptyArchive: true
            )

            echo 'Pipeline execution finished.'
        }
    }
}
