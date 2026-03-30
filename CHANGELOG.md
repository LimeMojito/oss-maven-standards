### 14.2.0

15be777 - [Release] updated version in pom.xml
be900cf - Merge pull request #10 from LimeMojito/feature/utilities-reafactor
5b3ac0a - Documentation warning removal.
e8734f1 - Remove incorrect use of inheritdoc.
f964e5f - Doco fixen
7328d3e - loc documentation warnings.
4ff4fde - SSM documentation warnings.
b936fc0 - SQS documentation warnings.
087d5a6 - Adjusted for build fails.
353655e - Moved aws-utilities to their own tree.  Updated tests, removed any SDK1 leftovers in dynamodb, confirmed lambda debug logs in docker-compose with localstack.
195d6d5 - Moved lock utilities to their own tree.
333efeb - Reworked dynamo db utilities to be SDK2 only, cleaner json implementation and use JsonLoader.
a0ee34c - Updated json utilities to use spring boot jackson autoconfiguration if the configuration class is used.
320c7fe - Refactor layout to better separate utility libraries and applications made with our own framework.
df888a2 - Refactor layout to better separate utility libraries and applications made with our own framework.
d91d116 - Documentation fixes.
b714d46 - Checkstyle
5a61a0d - Updated SNS subscription as newer localstack requires ARN rather than Q URL for subscriptions.
badef37 - Always overwrite.
11c1d4f - Fix SSM property naming.
07ccfa7 - Add sender to support configuration.
547a070 - Rework SQS to have a separated sender that uses consistent attributes when sending messages.  Removed SqsAsync client.  Spring Cloud SQS no longer necessary.
3103dc0 - Configs for localstack use the one URL.
c1318a1 - [Development] updated development version in pom.xml

