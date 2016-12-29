#!/bin/bash

SONATYPE_PASSWORD=""
read -s -p "Sonatype password:" SONATYPE_PASSWORD
echo "" # newline

SIGNING_KEY_PASSWORD=""
read -s -p "Secret key password:" SIGNING_KEY_PASSWORD
echo "" # newline

./gradlew clean uploadArchives \
    -PSONATYPE_NEXUS_PASSWORD="$SONATYPE_PASSWORD" \
    -Psigning.password="$SIGNING_KEY_PASSWORD"