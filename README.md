# YDA Framework

YDA is a software project designed to empower Java enterprises with cutting-edge AI Assistant capabilities. By adopting a modular approach, YDA enables the development of custom components, offering flexibility to either create tailored solutions or select from pre-built options. The YDA Framework helps developers integrate and rapidly launch virtual assistants on various platforms, simplifying the creation of assistants that can utilize platform data to provide relevant answers to user queries.

## Why is this project useful?

- **Bringing AI Assistant Technology to Java Enterprises**: YDA is pioneering the integration of AI assistant technology in the Java ecosystem, making it accessible to a wide range of enterprises that rely on this platform.
- **Data Ownership and Flexibility**: We empower enterprises to truly own their data, offering the flexibility to either deploy data in the cloud or manage it securely within their own infrastructure.
- **Modularity for Custom Solutions**: YDA’s modular architecture is designed with enterprise needs in mind, allowing businesses to easily customize and extend the framework to meet their specific requirements. Modularity is key in enterprise environments, and YDA delivers it effectively.

Building from Source
-------

You don’t need to build from source to use YDA (binaries in [repo.yda](https://github.com/love-vector/yda)), but if you want to try out the latest and greatest, YDA can be built and published to your local Maven cache using the Gradle wrapper. If you are using only YDA core functionality, JDK 11 is sufficient. For Spring integrations, JDK 17 is required.

```
$ ./gradlew publishToMavenLocal
```

This will build all of the jars and documentation and publish them to your local Maven cache.
It won't run any of the tests.
If you want to build everything, use the `build` task:

```
$ ./gradlew build
```

## Getting Started

### Dependencies

To integrate the YDA Framework into your project, add the following dependencies to your `build.gradle` (Gradle) file:

#### Gradle:

```groovy
dependencies {
    implementation "ai.yda:rag-starter"
    implementation "ai.yda:rag-assistant-starter"
    implementation "ai.yda:rest-spring-channel"
    implementation "ai.yda:openai-assistant-generator-starter"
    implementation "ai.yda:website-retriever-starter"
    implementation 'org.springframework.boot:spring-boot-starter'
    compileOnly "org.projectlombok:lombok"
    annotationProcessor "org.projectlombok:lombok"
}
```

### Modules

There are several key modules in the YDA Framework. Here is a quick overview:

#### WebsiteRetriever

The `WebsiteRetriever` module is designed to fetch and process information from websites. This module is responsible for scraping content from specified websites, processing the data, and loading it into a vector database such as Milvus for further use in AI-driven applications. Key features include:

-   **Web Scraping:** Collects data from web pages based on configured site maps and rules.
-   **Data Processing:** Cleans and preprocesses the scraped data to ensure consistency and quality.
-   **Vectorization:** Converts processed content into vector representations that can be stored in a vector database for efficient retrieval.

#### FileSystemRetriever

The `FileSystemRetriever` module is intended for processing files stored in local or networked file systems. This module reads various file formats, extracts relevant information, and loads it into the vector database. It is particularly useful for enterprises that need to integrate large volumes of document data into their AI systems. Key features include:

-   **File Parsing:** Supports a variety of file formats (e.g., PDF, DOCX, TXT) and extracts meaningful content.
-   **Data Normalization:** Standardizes and cleans the extracted data to make it ready for vectorization.
-   **Vector Storage:** Stores the vectorized file content in the database, making it available for AI applications and search.


You will also need to configure environment variables for normal operation
#### Environment Variables :


#### WebsiteRetriever
```
ai:
  yda:
    framework:
      rag:
        retriever:
          website:
            collectionName: websites
            sitemapUrl: ${SITE_MAP}
            topK: [SET_YOUR_VALUE]
            isProcessingEnabled: ${PROCESSING_ENABLE}
            clearCollectionOnStartup: ${CLEAR_COLLECTION_ON_STARTUP}
```

#### FileSystemRetriever
```
ai:  
  yda:  
    framework:  
      rag:  
        retriever:  
          filesystem:  
            localDirectoryPath:${FILE_PATH}
```

#### VectorStore
```
spring:  
  ai:  
    vectorstore:  
      milvus:  
        client:  
          host: ${MILVUS_HOST}  
          port: ${MILVUS_PORT}  
          username: ""  
  password: ""  
  databaseName: "default"  
  collectionName: "documents"  
  embeddingDimension: 1536  
  indexType: IVF_FLAT  
  metricType: COSINE  
  initializeSchema: ${ENABLE_INITIALIZE}  
    openai:  
      api-key: ${OPENAI_API_KEY}  
      chat:  
        options:  
          model: gpt-3.5-turbo  
          temperature: 0.7  
  embedding:  
        options:  
          model: text-embedding-3-small
```

#### Main:
```
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

Now you can send request to assistant and ask any question

### Request
```
http://localhost:8080/${ENDPOINT_RELATIVE_PATH}
```

Contributing
-------
For contribution guidelines, see [CONTRIBUTING](link).


## Requirements

### Core Framework
YDA Framework's core operates on Java 11. This allows for broader integration with existing enterprise systems that may still rely on Java 11.

### Integrations and Implementations
For integrations and specific implementations, YDA leverages Java 17 to take advantage of the latest language features and performance improvements. This provides a more modern and efficient environment for building and deploying AI-powered solutions.

### Additional Requirements
- **Spring Framework:** YDA heavily integrates with the Spring Framework. To fully utilize YDA's capabilities, your project must be set up within a Spring-based environment.
- **Vector Databases:** YDA leverages vector databases like Milvus for efficient data processing and retrieval.

### Best Practices
We recommend keeping your Java environment and dependencies up-to-date to ensure optimal performance and security. Given the importance of compatibility and security, staying current with the latest versions of Java and Spring Framework is highly advised.

### Compatibility and Configuration
YDA works seamlessly with your platform's native tools and libraries. However, to fully leverage its capabilities, including the advanced data processing features, ensure that your environment is configured with the required dependencies, particularly those related to Spring and vector databases.

## License
The YDA Framework is released under version 3 of the [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0-standalone.html).
