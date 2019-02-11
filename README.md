# NASA API Mars Curiosity Images Retriever

## Static Analysis
[Codacy: Review Dashboard](https://app.codacy.com/project/julianmpaul/MarsImages/dashboard)

### Prerequisites:
* maven v3.6 as of this writing.
* java v1.8

[Release Artifacts](https://github.com/julianmpaul/MarsImages/releases/tag/1.0)

### Building the project:
* `mvn clean package` command from the project root directory will create `MarsImages.jar`

### Running the application
* Required: jar (either from build or release)
* `java -jar MarsImages.jar` command from the project root directory will run the application. Open browser to `localhost:8080`
* `java -jar MarsImages.jar --server.port=<port>` to change the port

### Running the application: Docker
* Required: 1) Docker Installation 2) jar & Dockerfile (either from source or release)
* `java clean package` command from the project root directory to package the latest build
* `docker build -t <imagename>:<tag> .` command from the project root directory to build the docker image. Example: `docker build -t marsimgs:latest .` (period included)
* `docker run -d -p <specified port>:8080 <imagename>:<tag>` command to run the application in a container. Example: `docker run -d -p 8080:8080 marsimgs:latest`
* Open browser to `localhost:8080` or `localhost:<specified port>`

#### Notes:
* Images are saved in the same directory as the jar under 'curiosityImages'
* Each earth date will have its own subdirectory.