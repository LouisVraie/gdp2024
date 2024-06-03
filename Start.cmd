docker build --pull --rm -f "./Dockerfile" -t gestiondeprojet-loginapp:latest "."

docker network create -d bridge lb-net

docker rm -f registery && docker run -d -p 8081:8081 --net=lb-net --name registery --rm gestiondeprojet-loginapp

docker rm -f worker1 && docker run -d --net=lb-net --name worker1 -e HOSTNAME=worker1 gestiondeprojet-loginapp
docker rm -f worker2 && docker run -d --net=lb-net --name worker2 -e HOSTNAME=worker2 gestiondeprojet-loginapp
docker rm -f worker3 && docker run -d --net=lb-net --name worker3 -e HOSTNAME=worker3 gestiondeprojet-loginapp