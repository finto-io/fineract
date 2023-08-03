#!/bin/bash

FINERACT_EXT_VERSION=1.5.0

git clone https://github.com/apache/fineract.git
rm -rf fineract/custom/acme
mkdir fineract/custom/finto
cp -rf custom-module/* fineract/custom/finto/

cd fineract || exit
./gradlew :custom:docker:jibDockerBuild -D'jib.to.image'=nexus.finlab.dev/fineract-ext:${FINERACT_EXT_VERSION}
cd ..
rm -rf fineract

docker image push nexus.finlab.dev/fineract-ext:${FINERACT_EXT_VERSION}
