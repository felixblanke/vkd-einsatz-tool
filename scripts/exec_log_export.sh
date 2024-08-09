#! /bin/bash
java -jar einsatz-tool-0.3.3-SNAPSHOT-jar-with-dependencies.jar FINER "$@" 2>&1 | tee "`date +'%Y-%m-%dT%H:%M:%S%z'`.log"
