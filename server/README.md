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

### initial setup for local postgres development env
make sure to `source .env` first if you haven't

```bash
docker build . -t localdb -f store/devdb/Dockerfile --build-arg postgres_user=$POSTGRES_USER_DEV --build-arg postgres_password=$POSTGRES_PASSWORD_DEV --build-arg postgres_db=$POSTGRES_DB_DEV
```

```bash
docker run -p 5432:5432 localdb
```

connect to the db with interactive shell
```bash
docker exec -it <name of container> psql -U $POSTGRES_USER_DEV $POSTGRES_DB_DEV
```
the password is in the `.env` file as `$POSTGRES_PASSWORD_DEV`

TODO: set up RDS
- add to .env production DB creds
- modify existing code to write to DB vs store in in memory array
- clean up readme. add sections. 




## docker build image
build 
```bash
docker build --no-cache -t serverapp .
```
run
```bash
docker run -p 8080:8080 serverapp
```


## Deploy to production
#### build the image
```bash
docker build --no-cache -t serverapp .
```

#### push to aws ecr repo
You only need to do this step once (to login)
```bash
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.us-east-2.amazonaws.com
```
tag the image
```bash
docker tag serverapp:latest $AWS_ACCOUNT_ID.dkr.ecr.us-east-2.amazonaws.com/red-envelope:latest
```
push the image up to ECR
TODO this currently takes a pretty long time
```bash
docker push 000710763965.dkr.ecr.us-east-2.amazonaws.com/red-envelope:latest
```
deploy the latest image in ECR to app runner
```bash
aws apprunner start-deployment --service-arn $AWS_APPRUNNER_SERVICE_ARN
```


#### resources
https://docs.aws.amazon.com/AmazonECR/latest/userguide/getting-started-cli.html

### troubleshoot
- got 
```#8 0.313 Error: Could not find or load main class org.gradle.wrapper.GradleWrapperMain```
  when trying to run `docker build --no-cache -t serverapp .` to fix, run `gradle wrapper` first.  
  
#### docker troubleshoot
to start a shell to try out commands 
```bash
docker run --rm -it <id_last_working_image>  sh     
```

## future infrastructure upgrades
- use terraform for aws set up

