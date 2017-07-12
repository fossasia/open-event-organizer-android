#!/bin/sh
src='src/main/java/org/fossasia/openevent/app/data/models'
dest='src-delomboked'
cd app
if [ ! -f lombok.jar ]; then
    curl -O https://projectlombok.org/downloads/lombok.jar
fi
java -jar lombok.jar delombok $src -d $dest
rsync -a -r -v "$dest/" $src
rm -rf $dest
