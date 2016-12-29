Releasing
========

Prerequisites: make sure `SONATYPE_NEXUS_USERNAME`, `signing.keyId` and `signing.secretKeyRingFile` are set in `~/.gradle/gradle.properties`.

 1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
 2. Update the `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 5. If you don't like storing sonatype and private key passwords in `~/.gradle/gradle.properties` like me, run `bash upload-archives.sh`, it will prompt for them. Otherwise, just run `./gradlew clean uploadArchives`.
 6. Visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact (see [guide](http://central.sonatype.org/pages/releasing-the-deployment.html))
 7. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 8. Update the `gradle.properties` to the next SNAPSHOT version.
 9. `git commit -am "Prepare next development version."`
 10. `git push && git push --tags`

If step 5 or 6 fails, drop the Sonatype repo, fix the problem, commit, and start again at step 5.