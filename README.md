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

You can familiarize yourself with the setup instructions by visiting the following link: [get started](#).

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
