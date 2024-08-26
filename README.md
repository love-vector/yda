# YDA Framework

YDA Framework is a technology based on the principles of Retrieval-Augmented Generation (RAG), specifically designed for rapid creation and integration of artificial intelligence into web applications with minimal resource expenditure. Developed in Java, YDA provides a suite of built-in tools for processing large volumes of data, including functionalities for search, sorting, and data retrieval. These capabilities can be optionally disabled according to the user’s needs.

## What does this project do?

The YDA Framework helps developers integrate and rapidly launch virtual assistants on various platforms. The framework simplifies the creation of assistants that can utilize platform data to provide relevant answers to user queries.

## Why is this project useful?

- **Quick Integration**: Minimal effort required to add an assistant to an existing project.
- **Scalability**: Supports large platforms with high traffic.
- **Context Utilization**: Assistants use platform data and context to deliver relevant responses.

## Getting Started
- [Download the repository](https://github.com/love-vector/yda)
- build project
- publish to mavenLocal

  ![image](https://github.com/user-attachments/assets/dd4ac652-87a7-462a-93f0-fb973328a443)

- create your project and follow the steps below
### Example Usage

Before starting with YDA Framework, ensure your project is set up to include the dependencies listed below.

### Dependencies

To integrate the YDA Framework into your project, add the following dependencies to your `build.gradle` (Gradle) or `pom.xml` (Maven) file:

#### Gradle:

```groovy
plugins {
    id "java"
    id "org.springframework.boot" version "3.3.2"
    id "io.spring.dependency-management" version "1.1.6"
}

ext {
    ydaFrameworkVersion = "0.0.1"
}

bootJar {
    enabled = true
}

jar {
    enabled = false
}

group = 'org.projects'
version = '1.0-SNAPSHOT'

repositories {
    mavenLocal()
    mavenCentral()
    maven { url 'https://repo.spring.io/milestone' }
}

dependencies {
    implementation "ai.yda:rag-starter:${ydaFrameworkVersion}"
    implementation "ai.yda:rag-assistant-starter:${ydaFrameworkVersion}"
    implementation "ai.yda:rest-spring-channel:${ydaFrameworkVersion}"
    implementation "ai.yda:openai-assistant-generator-starter:${ydaFrameworkVersion}" // sse
    implementation "ai.yda:website-retriever-starter:${ydaFrameworkVersion}"
    implementation 'org.springframework.boot:spring-boot-starter'
    compileOnly "org.projectlombok:lombok:1.18.30"
    annotationProcessor "org.projectlombok:lombok:1.18.30"
    testImplementation platform('org.junit:junit-bom:5.9.1')
    testImplementation 'org.junit.jupiter:junit-jupiter'
}
```

#### Maven:
```
<properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <yda.framework.version>0.0.1</yda.framework.version>
        <spring.boot.version>3.3.2</spring.boot.version>
        <lombok.version>1.18.30</lombok.version>
    </properties>

    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
        </repository>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>ai.yda</groupId>
            <artifactId>rag-starter</artifactId>
            <version>${yda.framework.version}</version>
        </dependency>
        <dependency>
            <groupId>ai.yda</groupId>
            <artifactId>rag-assistant-starter</artifactId>
            <version>${yda.framework.version}</version>
        </dependency>

        <dependency>
            <groupId>ai.yda</groupId>
            <artifactId>rest-spring-channel</artifactId>
            <version>${yda.framework.version}</version>
        </dependency>

        <dependency>
            <groupId>ai.yda</groupId>
            <artifactId>openai-assistant-generator-starter</artifactId>
            <version>${yda.framework.version}</version>
        </dependency>

        <dependency>
            <groupId>ai.yda</groupId>
            <artifactId>website-retriever-starter</artifactId>
            <version>${yda.framework.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

You will also need to configure environment variables for normal operation
#### Environment Variables:
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
        generator:
          assistant:
            openai:
              assistant-id: ${ASSISTANT_ID}
      channel:
        rest:
          spring:
            endpoint-relative-path: ${ENDPOINT_RELATIVE_PATH}
            security-token: ${SECURITY_TOKEN}

spring:
  ai:
    vectorstore:
      milvus:
        client:
          host: ${MILVUS_HOST}
          port: ${MILVUS_PORT}
          username: ${USERNAME}
          password: ${PASSWORD}
        databaseName: ${DATABASE_NAME}
        collectionName: ${COLLECTION_NAME}
        embeddingDimension: 1536
        indexType: IVF_FLAT
        metricType: COSINE
        initializeSchema: ${ENABLE_INITIALIZE}
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: ${MODEL}
          temperature:${TEMPERATURE}
      embedding:
        options:
          model: ${EMBEDING_MODEL}
```
Lastly, you will need to configure your main class as shown in the following example

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

License
-------
```
                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
```

Requirements
-------
YDA Framework operates on Java 17+ and heavily integrates with the Spring Framework. If you plan to utilize the existing tools provided by YDA, you will need to implement it within a Spring-based environment.

YDA also leverages vector databases like Milvus for efficient data processing and retrieval.

We recommend keeping your Java environment and dependencies up-to-date to ensure optimal performance and security. Given the importance of compatibility and security, staying current with the latest versions of Java and Spring Framework is highly advised.

YDA works seamlessly with your platform's native tools and libraries. However, to fully leverage its capabilities, including the advanced data processing features, ensure that your environment is configured with the required dependencies, particularly those related to Spring and vector databases.