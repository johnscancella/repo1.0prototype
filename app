#!/usr/bin/env bash
#POSTIX

# Usage/help message 
function usage {
  echo -e "Usage: $0 <(-v|--VERSION) version> <(-t|--tag TAG)>
                 Where VERSION is the version to be in the produced component. Example: app-VERSION.zip
                 Where TAG is name to use in git when tagging this release
  " > /dev/stderr
}

#parse arguments
while :; do
  case $1 in
  -h|--help)
    usage
    exit 0;;
  -v|--version)
    VERSION=$1
    shift;;
  -t|--tag)
    TAG=$1
    shift;;
  -?*)
    printf 'WARN: Unknown option (ignored): %s\n' "$1" > /dev/stderr
  *) #if no more options stop looping
    break;;
  esac
  shift
done

gradle build -x test -Pversion=${VERSION}
//TODO upload files to artifactory
git tag ${TAG}
git push --tags
