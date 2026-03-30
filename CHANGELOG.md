### 14.1.0

531073a - [Release] updated version in pom.xml
04864b2 - Merge pull request #9 from LimeMojito/feature/add-lambda-localstack
1ab07ea - Updated readme.md
b3d1c56 - Updated semantic version due to API change
5d2ee31 - Remove wiremock from test utils as prefer docker and integration tests.
e3d47f5 - Updated readme.
0c74d72 - Refactored tests and utilities to use the same ObjectMapperPrototype and json Loader.   Updated javadoc and used AI for drafting to remove all warnings.
56618b7 - Adjust the lambda POC to use LambdaSupport and have an integration tests against docker.  Remove some unused build steps that were left over from the native experiment.
b13f328 - Add lambda support functions to work with localstack.  Add example of localstack configuration for lambda debugging.  S3 Support for dynamic creation and upload of data.  Added validation support to Jackson json testing.  Added support for AWS Lambda events which are serialized differently to Jackson defaults.
263f2a3 - Set java version to 17 for jenv
35f3ea7 - Merge remote-tracking branch 'origin/master'
a145cc8 - Fix for xmlunit vulnerability.
5f5cf59 - [Development] updated development version in pom.xml

