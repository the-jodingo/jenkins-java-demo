pipeline {
    agent any

    tools {
        // "maven-3.9" must exist under Manage Jenkins > Tools > Maven installations.
        // If you named yours differently, change this one line.
        maven 'maven-3.9'
        jdk   'jdk-17'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 15, unit: 'MINUTES')
    }

    environment {
        APP_NAME = 'jenkins-java-demo'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Branch: ${env.BRANCH_NAME ?: 'main'}"
                echo "Commit: ${env.GIT_COMMIT ?: 'n/a'}"
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn -B -DskipTests clean compile'
            }
        }

        stage('Test') {
            steps {
                // -B = batch mode (no colour codes in logs)
                sh 'mvn -B test'
            }
            post {
                always {
                    // Turns the surefire XML into the Jenkins test report
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests package'
            }
        }

        stage('Run demo') {
            steps {
                sh 'java -jar target/jenkins-java-demo.jar'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/jenkins-java-demo.jar', fingerprint: true
                archiveArtifacts artifacts: 'target/site/build-report.html', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'Build succeeded. Jar and HTML report are attached to this build.'
        }
        failure {
            echo 'Build failed. Check the stage that went red, then read its console output.'
        }
        always {
            cleanWs()
        }
    }
}
