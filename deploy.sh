
mvn clean install -DskipTests
docker build -f Dockerfile -t doan-1 .
docker save doan-1 -o doan-1.tar
scp -P 58882 doan-1.tar thangp@113.161.103.134:/home/thangp/doan-1
ssh thangp@113.161.103.134 -p 58882

#Pass server Thang!@#

docker rm -f doan-1
docker rmi doan-1
docker load < doan-1.tar
docker run --restart always -d  -p 8070:8088 --name doan-1 doan-1

#pass 123@4567
