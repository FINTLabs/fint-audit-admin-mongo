#!groovy
node {
    currentBuild.result = "SUCCESS"

    try {
        stage('checkout') {
            checkout scm
        }

        stage('build') {
            sh './gradlew clean build'
        }

        stage('deploy') {

        }
    }

    catch (err) {
        currentBuild.result = "FAILURE"
        throw err
    }
}