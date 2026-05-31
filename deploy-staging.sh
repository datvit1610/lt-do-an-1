echo "==================Start build source=================";
tag=1.0.30
echo  "Tags: $tag";
./mvnw verify -Pstaging -DskipTests
echo "Build source successfully";
echo "==================Start build docker=================";
docker build -t "pay-qr-be-staging:$tag" -f Dockerfile-staging .
sleep 10
echo "Build Successfully";
echo "Start push dockerhub";
docker tag "pay-qr-be-staging:$tag" "codechub/pay-qr-be-staging:$tag"
sleep 10
docker push  "codechub/pay-qr-be-staging:$tag"
echo "Push dockerhub successfully!";
sleep 10
docker image prune -f
echo "prune image!";
