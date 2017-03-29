# RUN DATABASES

docker run -d -p 6379:6379 redis:alpine

docker run --name some-postgres -e POSTGRES PASSWORD=mysecretpassword -d postgres


