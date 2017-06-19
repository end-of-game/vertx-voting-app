#!/usr/bin/env bash

docker login
docker push vertxswarm/verticle-vote
docker push vertxswarm/verticle-worker
docker push vertxswarm/verticle-result
docker logout

