#!groovy
node {
    currentBuild.result = "SUCCESS"
    env.DOCKER_PORT      = (env.BRANCH_NAME == 'master') ? 8102 : 10102;
    env.CONTAINER_SUFFIX = (env.BRANCH_NAME == 'master') ? "" : "_${env.BRANCH_NAME}"
    sh "echo Building branch: ${env.BRANCH_NAME} to fint-audit-admin-mongo${env.CONTAINER_SUFFIX}:${env.DOCKER_PORT}"

    try {
        stage('checkout') {
            checkout scm
        }

        stage('build') {
            sh './gradlew clean build'
        }

        stage('deploy') {
            sh 'chmod +x docker-build'
            sh 'sudo -E sh ./docker-build'
        }
    }

    catch (err) {
        currentBuild.result = "FAILURE"
        throw err
    }
}