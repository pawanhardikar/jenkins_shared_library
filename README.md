# Jenkins shared library helping build a cpp project

## Prerequisite

- A running jenkins instance, if you dont have one, see [how to install jenkins locally](#how-to-install-jenkins-locally)

## How to install Jenkins locally

- Download it from the official site https://www.jenkins.io/download/
- Once downloaded follow the instructions to install it and once it is done it automatically opens up a window for the initial setup.
- Initial Admin password can be found in secrets folder which is generally in Program Data->Jenkins->.jenkins.
- Then you can install suggested plugins which is recommended as it covers most of the important plugins or if you are sure what plugins you might need you can even install custom plugins.
- Once that is done you are good to go to use Jenkins and access it from http://localhost:8080 usually this is the deafult port that jenkins runs on but this can be changed during installation where you can test if this port is being used if yes you can change it explicitly.


## How to configure you shared library in Jenkins library

- Go to Manage Jenkins -> System -> (Search for Global Trusted Pipeline Libraries) -> Then you can give your shared library name -> Source management as Git or the one you will be using -> Provide the repository link where where the shared library file is present -> Click on Save.
- The version of the shared library can be made to point to branch, git tag or commit

Now you should be ready to consume shared library from your other repositories !!!

Demonstration to how the shared library is used by [consumers](https://github.com/pawanhardikar/my-cpp-project/blob/master/README.md)