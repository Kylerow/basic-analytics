# BASIC ANALYTICS
A basic analytics service to track user engagement.

## Tooling
To build, test and operate this system, you should have the following installed on your system:

> Scala v2.12.8 (The scala programming language)

> sbt v1.2.8 (Simple Build Tool - dependency management and building for scala)


## Building & Testing

### Unit Tests

To execute the unit test suite - the following command should be entered in the root of the project:
```
sbt test 
```

### Integration/Acceptance Tests

The integration/acceptance tests (including a baseline performance test) can be executed with the following command:
```
sbt -Danalytics.server.embedded=true it:test
```

This runs the suite against a web server started and managed by the test suite.  It's embedded so the developer doesn't have to bother with starting and stopping servers, and can debug through production code and integration test code side by side, with minimal ceremony.

## Running as Independent Service

To run this as a standalone service - simply enter the following command in the project root: 

```
sbt run
```