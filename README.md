# RUN DATABASES

docker run -d -p 6379:6379 --name vds-redis redis:alpine

docker run --name vds-postgres -p 5432:5432 -e POSTGRES_USER=postgres -d postgres

