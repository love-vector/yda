
# YDA – Open-Source Java AI Assistant

YDA is a software project designed to empower Java enterprises with cutting-edge AI Assistant capabilities. Adopting a modular approach, YDA enables the development of custom components, offering flexibility to create tailored solutions or select from pre-built options.

## Using YDA

YDA binaries are available at [repo.yda](#).    
To access the latest features, YDA can be built from source.    
JDK 11 is required for YDA core functionality, while JDK 17 is necessary for Spring integrations.

Building from Source
-------
To build YDA and publish to a local Maven Repository:  

```bash 
./gradlew publishToMavenLocal
 ``` 

This command builds all JARs and documentation, then publishes them to the local Maven Repository. Tests are not executed by this command. To build everything, including tests, use:  

```bash 
./gradlew build
 ```

## Getting Started

[The reference documentation](https://docs.yda-framework.ai/getting-started) includes detailed installation instructions as well as a comprehensive getting started guide.

Contributing
-------
For contribution guidelines, see [CONTRIBUTING](https://github.com/love-vector/yda/blob/main/CONTRIBUTING.md).

## Requirements

### Core
YDA core operates on Java 11.

### Integrations and Implementations
YDA currently provides integrations and implementations exclusively for the Spring Framework, relying on Java 17. RAG implementation relies on [Milvus](https://milvus.io/) and [OpenAI](https://platform.openai.com/docs/assistants/overview).

## License
YDA is released under version 3 of the [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0-standalone.html).
