set -e
mvn package
cp target/einsatz-tool-0.3.3-SNAPSHOT-jar-with-dependencies.jar .
./scripts/exec.sh $1
