echo "==================Start build source=================";
tag=1.0.14
echo  "Tags: $tag";
./mvnw verify -Pprod -DskipTests
echo "Build source successfully";
echo "==================Start build docker=================";
docker build -t "ads_ssp_prod_be:$tag" -f Dockerfile-prod .
sleep 5
echo "Build Successfully";
echo "Start push dockerhub";
docker tag "ads_ssp_prod_be:$tag" "codechub/ads_ssp_prod_be:$tag"
sleep 5
docker push  "codechub/ads_ssp_prod_be:$tag"
echo "Push dockerhub successfully!";
sleep 5
docker image prune -f
echo "prune image!";
