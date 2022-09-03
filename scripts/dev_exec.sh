set -e
mvn package
cp target/einsatz-tool-0.3.2-SNAPSHOT-jar-with-dependencies.jar .
./exec.sh $1
