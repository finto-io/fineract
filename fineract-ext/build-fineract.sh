#!/bin/bash

FINERACT_EXT_VERSION=1.12.0

git clone https://github.com/apache/fineract.git
cd fineract || exit
git reset --hard 7741340ef302e0e651e46cd6f4d80bc010f54e4b
rm -rf ./custom/acme
mkdir ./custom/finto
cp -rf ../custom-module/* ./custom/finto/
