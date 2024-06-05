docker build --pull --rm -f "./Dockerfile" -t gdp-projectapp:latest "."

docker network create -d bridge lb-net

docker rm -f loadbalancer && docker run -d -p 8081:8081 --net=lb-net --name loadbalancer -e HOSTNAME=loadbalancer gdp-projectapp

docker rm -f registry && docker run -d  --net=lb-net --name registry -e HOSTNAME=registry gdp-projectapp

docker rm -f node1 && docker run -d --net=lb-net --name node1 -e HOSTNAME=node1 gdp-projectapp
docker rm -f node2 && docker run -d --net=lb-net --name node2 -e HOSTNAME=node2 gdp-projectapp