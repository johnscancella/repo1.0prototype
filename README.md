# repo1.0prototype
prototype java implementation of the repo 1.0 re-write.

## Major Features
* [x] message based system for robust and scalable architecure
* [x] retrieve files via a scp request (for really big files)
* [x] receive files via rest (post and put)
* [x] return files via rest (get)
* [x] continous and programanic verification of files via hashes
* [x] flexable metadata storage that is loosely coupled with object store
* [x] Secure https requests
* [x] no known hash colisions due to using SHA-256
* [ ] compatible with AWS
* [ ] secure microservices using oauth
* [ ] integration with user authentication
* [ ] easy to use GUI for content custodians
* [ ] automatic deploy

## Build System requirements
* Java 8+ Development Kit
* Gradle
* Internet access (or all project dependencies in a local maven structure repository)
 
## Third party dependencies
* Java 8+
* RabbitMq (for message passing)
* MongoDb

## Eclipse IDE
To generate Eclipse IDE projects run `gradle eclipse` from the root directory

## Building
Gradle commands run from the root directory (i.e. the one where this file lives) are applied to all projects. So if you run `gradle build` it will run the build command on all the subprojects. Conversely, if you run the build in a project directory it will only build that project (and it's dependencies).
Built components (rpm, deb, zip, etc.) are located in each projects build folder.
For more general info about gradle and how it works see [gradle userguide](https://docs.gradle.org/current/userguide/userguide.html)

### Building with a specific version
To build version specific components (rpm, deb, zip, etc.) add `-Pversion=FOO_VERSION` where `FOO_VERSION` is replaced with the actual version you want. It is string based so `[a-zA-Z0-9.-_]` is allowed.

### Building an RPM package
run `gradle buildRpm`

### Building an DEB package
run `gradle buildDeb`

### Building an Zip package
run `gradle distZip`

## Public domain
This project is in the worldwide [public domain](LICENSE.md).

> This project is in the public domain within the United States, and copyright and related rights in the work world wide are waived through the [CC0 1.0 universal public domain dedication](https://creativecommons.org/publicdomain/zero/1.0/).
>
> All contributions to this project will be released under the CC0
>dedication. By submitting a pull request, you are agreeing to comply
>with this waiver of copyright interest.
