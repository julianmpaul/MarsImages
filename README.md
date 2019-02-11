# NASA API Mars Curiosity Images Retriever

## Static Analysis
https://app.codacy.com/project/julianmpaul/MarsImages/dashboard

### Prerequisites:
* maven - as of this writing mvn 3.6
* java - 1.8

### Building the project:
* `mvn clean package` command from the project root directory will create `MarsImages.jar`
### Running the application
* `java -jar MarsImages.jar` will run the application. Open browser to `localhost:8080`
* `java -jar MarsImages.jar --server.port=<port>` to change the port

### Notes:
* Images are saved in the same directory as the jar under 'curiosityImages'
* Each earth date will have its own subdirectory.
