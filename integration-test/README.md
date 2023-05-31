# -finto-fineract-integration

## In case of DB script were updated (only 01-init-postgres.sh script!): 
1. Build locally docker image:
   1. cd ./integration-test/src/main/resources
   2. docker build nexus.finlab.dev/fineractpostgresql:{new version here}
2. Push docker image to nexus:
   1. docker push nexus.finlab.dev/fineractpostgresql:{new version here}
3. Update integration-test/src/main/resources/docker-compose.yml:
   1. Set the correct version of nexus.finlab.dev/fineractpostgresql (on the line 5) 