// define the pipeline using the declarative syntax
// we use Jenkinsfile to define the pipeline
pipeline {
    
    // use jenkins node as the agent
    // this means that the pipeline can run on any available agent
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven-3.8.4'
    }

    environment {
        // define the SonarQube server URL and credentials
        SONARQUBE_SERVER = 'SonarCloud'
        SONAR_PROJECT_KEY = 'ashish-panicker_simple-spring-api'
        // Display Name in SonarQube
        SONAR_PROJECT_NAME = 'simple-spring-api' 
        SONAR_ORGANIZATION = 'ashish-panicker'

        // --- Docker / Deploy ---
        APP_NAME              = 'simple-spring-api'
        // <username>/<repo>
        DOCKER_IMAGE          = "ashishspanicker/${APP_NAME}"    
        CONTAINER_NAME        = 'simple-spring-api'
        // container port your app listens on
        APP_PORT              = '9595'                          
        // Jenkins credential (username+password) for registry login
        DOCKERHUB_CREDENTIALS = 'docker-credentials'
        // Optional: set a host port different from container port (e.g., '9595:9595')
        HOST_PORT_MAPPING     = '9595:9595'

    }

    stages {
        // stage to checkout the code from the repository
         stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                // by default jenkins will use the master branch
                // if you want to use a different branch, specify it here
                git branch: 'main', url: 'https://github.com/ashish-panicker/simple-spring-api.git'
            }
        }

        // stage to build the application using maven
        stage('Build') {
            steps {
                echo 'Building with Maven...'
                sh 'mvn clean install'
            }
        }

        // stage to run tests
        // this stage will run the tests using maven
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            // post actions for the test stage
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // stage to perform static code analysis using SonarQube
        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.organization=${SONAR_ORGANIZATION} \
                        -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                    """
                }
            }
            // post actions for the SonarQube analysis stage
            post {
                always {
                    echo 'SonarQube analysis completed.'
                }
            }
        }

        stage('Quality Gate Check') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    echo 'Waiting for SonarQube quality gate...'
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        // stage to build the Docker image
        stage('Build Docker Image') {
            steps {
                script {
                    // Use a deterministic image tag per build (BUILD_NUMBER) and also tag as 'latest'
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}"
                }
                sh """
                    echo "Building Docker image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    docker build --pull -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest .
                """
            }
        }

         stage('Docker Push Image') {
            // when {
            //     branch 'main'
            // }
            steps {
                echo "Logging into registry and pushing ${DOCKER_IMAGE}:${IMAGE_TAG}"
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDENTIALS}",
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    sh '''
                        echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout || true
                    '''
                }
            }
        }
    }

    post {
        // post actions for the entire pipeline
        always {
            echo 'Pipeline succeeded with status: ${currentBuild.currentResult}'
        }
    }

}

// docker run -d --name simple-spring-api -p 9595:9595 simple-spring-api:latest

