// define the pipeline using the declarative syntax
// we use Jenkinsfile to define the pipeline
pipeline{
    
    // use jenkins node as the agent
    // this means that the pipeline can run on any available agent
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven-3.8.4'
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
                bat 'mvn clean install'
            }
        }

        // stage to run tests
        // this stage will run the tests using maven
        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test'
            }
            // post actions for the test stage
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // stage to deploy the application
        // this stage will deploy the application to a server
        stage('Deploy') {
            steps {
                echo 'Deploying to a server...'
                echo 'Deployment successful!'
            }
        }
    }

}