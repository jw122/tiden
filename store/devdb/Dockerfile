## Dockerfile for spinning up local postgres instance for running services locally
FROM postgres:13.3-alpine

ARG postgres_user
ARG postgres_password
ARG postgres_db

RUN if [ -z $postgres_password ]; then echo 'variable postgres_password must be specified. Exiting.'; exit 1; fi

ENV POSTGRES_USER $postgres_user
ENV POSTGRES_PASSWORD $postgres_password
ENV POSTGRES_DB $postgres_db
ADD store/migrations/*.sql /docker-entrypoint-initdb.d/