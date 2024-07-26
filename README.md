# YDA
## yda-application

As developers, we use ['yda-application' repository](https://github.com/love-vector/yda-application) to run and test the YDA framework.
The 'yda-application' project relies on modules from this repository, so we use `maven-publish` plugin to publish artifacts to Local Maven Repository.
Also, the Gradle task `publishToMavenLocal` is automatically triggered when task `build` is executed.