// vars/cppBuild.groovy
def call(Map config = [:], Closure body) {
    def buildType = config.buildType ?: 'Release'
    def cmakeCommand = config.cmakeCommand ?: "cmake -S. -Bbuild -DCMAKE_BUILD_TYPE=${buildType}"
    def runTests = config.runTests ?: true
    def artifactPattern = config.artifactPattern ?: 'build/bin/my_app'
    def buildDir = config.buildDir ?: 'build'

    def getArtifactPath = {
        def path = artifactPattern
        if (!artifactPattern.contains('/')) {
            path = "${buildDir}/${artifactPattern}"
        }
        if (artifactPattern.startsWith('.')) {
            path = artifactPattern
        }
        return path
    }

    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = this

    node {
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            dir('.') {
                bat "${cmakeCommand}"
                bat "cmake --build ${buildDir} --config ${buildType}"
            }
        }

        stage('Test') {
            if (runTests) {
                try {
                    dir("${buildDir}") {
                        bat 'ctest --verbose'
                    }
                } catch (Exception e) {
                    echo "Tests Failed, check the logs"
                }
            } else {
                echo "Skipping tests."
            }
        }

        stage('Package Artifact') {
            def artifactPath = getArtifactPath()
            echo "Archiving artifact: ${artifactPath}"
            bat "dir ${buildDir}" // Debug
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

def createArtifact(Map args) {
    def name = args.name
    def type = args.type
    echo "Creating artifact: Name: ${name}, Type: ${type}"
    bat "echo Creating ${name}.${type}"
}
