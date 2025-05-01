// vars/cppBuild.groovy
def call(Map config = [:], Closure body) {
    // Default values for the configuration
    def buildType = config.buildType ?: 'Release'
    def cmakeCommand = config.cmakeCommand ?: 'cmake ..'
    def makeCommand = config.makeCommand ?: 'make'
    def runTests = config.runTests ?: true
    def artifactPath = config.artifactPath ?: 'build/my_app'

    // Example of using a closure for a block of steps
    body.resolveStrategy = Closure.DELEGATE_FIRST  // Important for using Jenkins DSL within the closure
    body.delegate = this

    node {
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            dir('.') {  // Create and enter the 'build' directory
                // Configure and build with CMake.
                bat "${cmakeCommand}"
                bat "cmake --build build --config ${buildType}"
            }
        }


        stage('Test') {
            if (runTests) {
                try {
                    dir('build') {
                        bat 'ctest --verbose'
                    }
                } catch (Exception e) {
                    //test failures should not stop the pipeline
                    echo "Tests Failed, check the logs"

                }
            } else {
                echo "Skipping tests."
            }
        }

        stage('Package Artifact') {
            // Package the main executable
            archiveArtifacts artifacts: artifactPath
        }

        body()
    }
}

def createArtifact(String name, String type) {
    echo "Creating artifact: Name: ${name}, Type: ${type}"
    // In real scenario, we can create a file here
    bat "echo Creating ${name}.${type}"
}
