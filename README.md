# Crowdsourcing Transcription Platform
Web application that allows users to collaborate on the transcription of old documents.

## Software used:
1. Java JDK 11
2. NodeJS w/ npm
3. Maven
4. FusionAuth
5. Docker
NOTE: If running with Docker, all you need to install on the host is Docker

## To run on Docker:
1. Install Docker on host machine
1. Retrieve the .jar, Dockerfile, and docker-compose.yml from the repository
3. Run the command "docker build -t printapi ." to build the docker image
4. Run the command "docker-compose up" to run the application

## To build a new jar:
1. At the root of the backend directory run the command "mvn clean install"
2. /target will have the new .jar

## FusionAuth Config 
If you are not importing a .sql file with fusionauth data you will have to 
configure fusionauth from scratch. Luckily this is a very simple process. 
Temporarily remove the "app:" section from the docker-compose.yml and run the command
"docker-compose up". From there navigate to https://localhost:9011 and begin
the fusionauth instructions at "Complete maintenance mode and setup wizard"
- https://fusionauth.io/docs/v1/tech/5-minute-setup-guide

## Configure the docker-compose.yml
- MySQL (https://hub.docker.com/_/mysql)
environment:
  Initially we have it configured to have a root user with an empty password.
  You may change this to be more secure.
ports:
  The format is "hostingServerPort:dockerContainerPort".
  It is strongly recommended to leave the dockerContainerPort as 3306, 
  however the hostingServerPort can be any open port on the physical machine.

- app (This is running the jar image "printapi")
 environment:
  SPRING_DATASOURCE_USERNAME: <- MySQL root user
  SPRING_DATASOURCE_PASSWORD: <- MySQL root user's password
  
- fusionauth 
(https://fusionauth.io/docs/v1/tech/installation-guide/docker) (https://hub.docker.com/r/fusionauth/fusionauth-app)

## Docker Volumes
- To have persistent data this docker-compose.yml is using volumes. When moving data between
machines it is recommended to use mysqldump, however it is possible to move entire volumes. There
are further instructions at https://hub.docker.com/_/mysql and https://docs.docker.com/storage/.

