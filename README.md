# Facebook Messenger API Client
This is a Java client for the Facebook Messenger API. It is essentially a thin wrapper around it. For more information about the API, please refer to Facebook's developer documentation:
https://developers.facebook.com/docs/messenger-platform

This client is still a work in progress. Most but not all API functionality is supported. We are working towards exhaustively supporting all features. We are also open to contributions that help us get there.

# Usage
An instance of `FacebookMessengerClient` is needed to validate and deserialize inbound requests as well as to send messages. Using a no argument constructor should be sufficient. That will use the v2.6 Facebook Messenger end point.

`FacebookMessengerClient.deserializeCallback` deserializes inbound request bodies into POJOs found in `com.replyyes.facebook.messenger.bean`. All client methods for sending messages take a Facebook page access token as an argument. As such, a single instance of the client can be used to handle message correspondence for many Facebook pages at the same time.

We are using Jackson's `ObjectMapper` to serialize and deserialize request bodies. Consequently, POJO names and properties closely resemble API object and property names. Further explanation of the Facebook Messenger API is left to their developer documentation.

# How To Contribute
We are primarily interested in improving our coverage of the Messenger API however, we are also open to utilities. Just send us a PR.

# Build Notes
Deploying new artifacts to Maven Central requires jars for Javadoc and source code. The following command will generate, sign, and upload them to the staging repository: `mvn clean javadoc:jar source:jar package gpg:sign deploy`
