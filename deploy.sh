
mvn clean install -DskipTests
docker build -f Dockerfile -t codec-qlts-be .
docker save codec-qlts-be -o codec-qlts-be.tar
scp -P 58882 codec-qlts-be.tar thangp@113.161.103.134:/home/thangp/codec-qlts
ssh thangp@113.161.103.134 -p 58882

#Pass server Thang!@#

docker rm -f codec-qlts-be
docker rmi codec-qlts-be
docker load < codec-qlts-be.tar
docker run --restart always -d  -p 8195:8088 --name codec-qlts-be codec-qlts-be

#pass 123@4567
