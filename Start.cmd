docker build --pull --rm -f "./Dockerfile" -t gdp-projectapp:latest "."

docker network create -d bridge lb-net

docker rm -f loadbalancer && docker run -d -p 8081:8081 --net=lb-net --name loadbalancer -e HOSTNAME=loadbalancer gdp-projectapp

docker rm -f registry && docker run -d  --net=lb-net --name registry -e HOSTNAME=registry gdp-projectapp

docker rm -f worker1 && docker run -d --net=lb-net --name worker1 -e HOSTNAME=worker1 -e TYPE=hello gdp-projectapp
docker rm -f worker2 && docker run -d --net=lb-net --name worker2 -e HOSTNAME=worker2 -e TYPE=hello gdp-projectapp
docker rm -f worker3 && docker run -d --net=lb-net --name worker3 -e HOSTNAME=worker3 -e TYPE=chat gdp-projectapp
docker rm -f worker4 && docker run -d --net=lb-net --name worker4 -e HOSTNAME=worker4 -e TYPE=chat gdp-projectapp