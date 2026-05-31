node {
    stage('checkout') {
        checkout scm
    }

    stage('check docker') {
        sh "docker --version"
    }

    stage('down') {
    	sh "docker-compose -p api-gateway-dev -f ./ci_cd/dev/docker-compose.yml down -v --remove-orphans --rmi all"
    }

    stage('deploy') {
        sh "docker-compose -p api-gateway-dev -f ./ci_cd/dev/docker-compose.yml up --build -d"
    }
}
