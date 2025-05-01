// vars/cppBuild.groovy
def call(Map config = [:], Closure body) {
    // Default values for the configuration
    def buildType = config.buildType ?: 'Release'
    def cmakeCommand = config.cmakeCommand ?: 'cmake -S. -Bbuild -DCMAKE_BUILD_TYPE=${buildType}'
    def runTests = config.runTests ?: true
    def artifactPattern = config.artifactPattern ?: 'build/bin/my_app' // Default artifact pattern
    def buildDir = config.buildDir ?: 'build' // Added build directory parameter

    // Helper function to get artifact path
    def getArtifactPath = { ->
        def path = artifactPattern
        if (!artifactPattern.contains('/')) {
            path = "${buildDir}/${artifactPattern}"
        }
        //handle relative paths
        if (artifactPattern.startsWith('.')){
            path = artifactPattern
        }
        return path
    }

    // Example of using a closure for a block of steps
    body.resolveStrategy = Closure.DELEGATE_FIRST  // Important for using Jenkins DSL within the closure
    body.delegate = this

    node {
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            dir('.') {  // Changed to the root of the project. CMakeLists.txt should be here.
                // Configure and build with CMake.
                bat "${cmakeCommand}"
                bat "cmake --build ${buildDir} --config ${buildType}" // Use the buildDir
            }
        }

        stage('Test') {
            if (runTests) {
                try {
                    dir("${buildDir}") { //and here
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
            def artifactPath = getArtifactPath()
            echo "Archiving artifact: ${artifactPath}"  // Print the artifact path
            try {
              archiveArtifacts artifacts: artifactPath
            } catch (Exception e) {
              echo "Error archiving artifacts: ${e.message}"
              currentBuild.result = 'FAILURE'
              error "Artifact archiving failed"
            }
        }

        body()
    }
}

def createArtifact(String name, String type) {
    echo "Creating artifact: Name: ${name}, Type: ${type}"
    // In real scenario, we can create a file here
    bat "echo Creating ${name}.${type}"
}
