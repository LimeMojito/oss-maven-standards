### 14.0.18

cf63d2d - [Release] updated version in pom.xml
e462407 - GW-498: Manual version update.
cb83794 - GW-498: Checkout master before pushing as previous state was detached.  Release builds are always on master.
ae83606 - GW-498: Perform versions updates and commit before release build.  Release build from tag.  Push after build.
d13a6d9 - GW-498: Do not cancel in progress.
1ab171c - GW-498: Enable deferred deploy mode for releases.
ad57d0f - GW-498: Concurrency group to stop builds overlapping on the AWS account.
1c7d0ff - GW-498: handle concurrent development update during long build.
31a70d6 - GW-498: Update development version.
6e1cd2d - GW-498: Don't increment a patch twice.

