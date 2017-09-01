sbt clean
sbt docker:publishLocal
image_id=$(docker images -q | head -n 1)
docker run -it --rm -v $(pwd)/src:/src $image_id