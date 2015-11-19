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

## Quickstart
To build run `gradle buildRpm`
This will create multiple `.rpm` files that can then be installed on your system of choice
Or you can run a project locally by changing into the directory of the project you want to deploy and running `gradle bootRun`

### Public domain
This project is in the worldwide [public domain](LICENSE.md).

> This project is in the public domain within the United States, and copyright and related rights in the work world wide are waived through the [CC0 1.0 universal public domain dedication](https://creativecommons.org/publicdomain/zero/1.0/).
>
> All contributions to this project will be released under the CC0
>dedication. By submitting a pull request, you are agreeing to comply
>with this waiver of copyright interest.
