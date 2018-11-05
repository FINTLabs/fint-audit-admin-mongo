pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                sh 'git clean -fdx'
                sh "docker build -t ${GIT_COMMIT} ."
            }
        }
        stage('Publish Latest') {
            when {
                branch 'master'
            }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/audit-admin-mongo:latest"
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh 'docker push dtr.fintlabs.no/beta/audit-admin-mongo:latest'
                }
            }
        }
        stage('Publish Version') {
            when {
                tag pattern: "v\\d+\\.\\d+\\.\\d+(-\\w+-\\d+)?", comparator: "REGEXP"
            }
            steps {
                script {
                    VERSION = TAG_NAME[1..-1]
                }
                sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/audit-admin-mongo:${VERSION}"
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker push dtr.fintlabs.no/beta/audit-admin-mongo:${VERSION}"
                }
            }
        }
        stage('Publish PR') {
            when { changeRequest() }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/audit-admin-mongo:${BRANCH_NAME}"
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker push 'dtr.fintlabs.no/beta/audit-admin-mongo:${BRANCH_NAME}'"
                }
            }
        }
        /*
        stage('Coverage') {
            agent {
                docker {
                    label 'docker'
                    image 'gradle:4.9.0-jdk8-alpine'
                }
            }
            environment {
                COVERALLS_REPO_TOKEN = ''
            }
            steps {
                sh 'gradle --no-daemon check jacocoTestReport coveralls'
            }
        }
        */
    }
}
