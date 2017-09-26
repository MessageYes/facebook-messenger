# Facebook Messenger API Client
This is a Java client for the Facebook Messenger API. It is essentially a thin wrapper around it. For more information about the API, please refer to Facebook's developer documentation:
https://developers.facebook.com/docs/messenger-platform

This client is still a work in progress. Most but not all API functionality is supported. We are working towards exhaustively supporting all features. We are also open to contributions that help us get there.

# Usage
An instance of `FacebookMessengerClient` is needed to validate and deserialize inbound requests as well as to send messages. Using a no argument constructor should be sufficient. That will use the v2.6 Facebook Messenger end point.

`FacebookMessengerClient.deserializeCallback` deserializes inbound request bodies into POJOs found in `com.messageyes.facebook.messenger.bean`. All client methods for sending messages take a Facebook page access token as an argument. As such, a single instance of the client can be used to handle message correspondence for many Facebook pages at the same time.

We are using Jackson's `ObjectMapper` to serialize and deserialize request bodies. Consequently, POJO names and properties closely resemble API object and property names. Further explanation of the Facebook Messenger API is left to their developer documentation.

# How To Contribute
We are primarily interested in improving our coverage of the Messenger API however, we are also open to utilities. Just send us a PR.

# Build Notes
Deploying new artifacts to Maven Central requires jars for Javadoc and source code. The following command will generate, sign, and upload them to the staging repository: `mvn clean javadoc:jar source:jar package gpg:sign deploy`

In order for the above command to work, you will need to have GPG installed. The Sonatype documentation is a pretty good resource on getting it setup:
http://central.sonatype.org/pages/working-with-pgp-signatures.html

It is probably also worth reviewing the Sonatype documentation on using Maven to build artifacts:
http://central.sonatype.org/pages/apache-maven.html

Ultimately, you will need to have a `~/.m2/settings.xml` that looks very similar to the following:
```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR SONATYPE USERNAME</username>
      <password>YOUR SONATYPE PASSWORD</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
        <gpg.passphrase>YOUR GPG PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```
