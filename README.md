# RUN DATABASES

docker run -d -p 6379:6379 redis:alpine

docker run --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -d postgres

