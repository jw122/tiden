### the kotlin server app

[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)


# Creating an HTTP API with Ktor

This repository is the code corresponding to the hands-on lab [Creating HTTP APIs](https://ktor.io/docs/creating-http-apis.html).


## initial setup
- install [docker](https://docs.docker.com/get-docker/)
- install awscli `brew install awscli`
- install gradle `brew install gradle`
- set up awscli `aws configure`, if you need an aws access key ping your aws admin to help set up (it's in aws IAM)

in root directory `/red-envelopes` run
```bash
source .env
```

## docker build image
build 
```bash
docker build --no-cache -t demo .
```
run
```bash
docker run -p 8080:8080 demo
```


### aws ecr
#### push to aws ecr repo
```bash
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.us-east-2.amazonaws.com
```
```bash
docker tag demo:latest $AWS_ACCOUNT_ID.dkr.ecr.us-east-2.amazonaws.com/red-envelope:latest
```
TODO this currently takes a pretty long time
```bash
docker push 000710763965.dkr.ecr.us-east-2.amazonaws.com/red-envelope:latest
```
#### deploy with aws app runner

```bash
aws apprunner start-deployment --service-arn $AWS_APPRUNNER_SERVICE_ARN
```
then enter
```json
{
  "ServiceArn": "$AWS_APPRUNNER_SERVICE_ARN"
}
```

#### resources
https://docs.aws.amazon.com/AmazonECR/latest/userguide/getting-started-cli.html

### troubleshoot
- got 
```#8 0.313 Error: Could not find or load main class org.gradle.wrapper.GradleWrapperMain```
  when trying to run `docker build --no-cache -t demo .` to fix, run `gradle wrapper` first.  
  
#### docker troubleshoot
to start a shell to try out commands 
```bash
docker run --rm -it <id_last_working_image>  sh     
```

## future infrastructure upgrades
- use terraform for aws set up

