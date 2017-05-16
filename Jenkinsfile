stage "Init SCM"
node {
    properties([disableConcurrentBuilds()])
    checkout scm
}
stage "Check tests"
node {
    sh 'mvn clean package docker:build'
}
