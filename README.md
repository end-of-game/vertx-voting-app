# RUN DATABASES

docker run -d -p 6379:6379 redis:alpine

docker run --name postgres --net host -e POSTGRES_USER=postgres -d postgres

