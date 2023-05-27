//Declarative pipeline
pipeline{
    agent any
    parameters {
        string(name: 'BRANCH_SOURCE', defaultValue: 'source', description: 'Enter source code branch ')
        string(name: 'BRANCH_PIPE', defaultValue: 'pipeline', description: 'Enter pipeline code branch')
        string(name: 'SERVER_IP', defaultValue: '', description: 'Enter server ip')
        
    }
    stages{
        stage("clone code"){
            steps{
                println "here im cloning the code from github"
                    git branch: '$BRANCH_SOURCE', 
                     url: "https://github.com/pandu1031/boxfuse-sample-java-war-hello.git"

            }
        }
        stage("Build"){
            steps{
                println " here im building the code"
                sh "mvn clean package"
                sh " ls -l target/"
            }
        }
        stage("Upload artifacts"){
            steps{
                println " here im uploading artifacts to s3 bucket"
                sh "aws s3 cp target/hello-${BUILD_NUMBER}.war s3://mamuu/pandu/${BRANCH_SOURCE}/${BUILD_NUMBER}/"
            }
        }
        stage("Deploy"){
            steps{
                println "here im downloading artifacts from jenkins server to tomcat"
                sh """
                scp -i /tmp/mamu1031.pem /tmp/tomcatinstall.sh ec2-user@${SERVER_IP}:/tmp/
                ssh -i /tmp/mamu1031.pem ec2-user@${SERVER_IP} "bash /tmp/tomcatinstall.sh && systemctl status tomcat"
                """
            }
        }
    }
}