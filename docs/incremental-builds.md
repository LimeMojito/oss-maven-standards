# Performing Incremental Builds and Releases
Build system 18 introduces incremental feature builds and releases to Maven Central.  This is to improve build practices, support aligning private commercial builds to mono-repos for AI, and live with the soon-to-be introduced monthly release limits for open source on Maven Central (11/Aug/2026).

We use the [multi-module-maven-release-plugin](https://github.com/danielflower/multi-module-maven-release-plugin) by @danielflower to manage incemental releases, where the system computes build numbers based on changes in  _per module tagging_.  This reduces our output to Maven Central to the minimal number of modules to keep montly releases under the ~80MB limit.  Note security library updates will generate **ALL** modules due to our centralized dependency management.  We have limited OSS deployments to Maven Central to 300MB/Month.

Feature branch builds are using the [gitflow-incremental-builder](https://github.com/gitflow-incremental-builder) by @famod, activated by the maven profile ```-Pincremental```.  This plugin is configured to compare against the default branch of the repository (master/main) and only build those modules that have altered.  Note that git setup
includes a URL rewrite to include the token for github so that authetication behaves.  See [Lime Git Action](../.github/actions/lime-env-setup-git/action.yml)
          
## Keeping large applications OFF of Maven Central
Due to the new upload restrictions, large applications should not be published to Maven Central.  To enable this we have to edit in two places for the cleanest builds:
1. In ```lime-oss-maven-standards-bom/pom.xml``` we updated the BOM generator execution configuration to have dependency exclusions for the large artifacts so they don't appear in the published BOM.
  ```xml
    <dependencyExclusions>
        <dependency>
            <groupId>--groupid--</groupId>
            <artifactId>--artifactId--</artifactId>
        </dependency>
    </dependencyExclusions>
  ``` 
2. In the ```pom.xml``` of the module itself we add an exckusion for the release plugin. 
   ```xml
    <build>
     <plugins>
         <plugin>
             <!-- Do not upload large artifacts to central -->
             <groupId>org.sonatype.central</groupId>
             <artifactId>central-publishing-maven-plugin</artifactId>
             <configuration>
                 <skipPublishing>true</skipPublishing>
             </configuration>
         </plugin>
     </plugins>
     </build>
   ``` 

## Recovering from a botched Maven Central Release

If you have released a version of your library to Maven Central and it is broken, you can use the following steps to
recover:

1. Delete all tags from the repository associated with the build number.  This is to allow the release plugin to "redo" the altered version calculation from the previous successful release.
2. If the error was a missed "publish" step in [maven central](https://central.sonatype.com/publishing/deployments).
   1. Drop the failed deployment.
   2. Update some source code (pom.xml / src) to register a change on any module.
   3. push or merge PR to master, triggering a release build.
   4. Check the deployment in maven central, checking pom version numbers are aligned with released binaries on central.
   5. publish the release at maven central.
3. Else
   1. Merge changes to main as per normal. 
   2. Don't forget to publish the deployment on maven central (assuming under monthly limits) at [maven central](https://central.sonatype.com/publishing/deployments) 
                 
## Doing development based on oss-maven-standards
For doing development with Lime Mojito's ```oss-maven-standards```, we recomend imports as below so you are aligned to our depdendencies.  Utilities will need to be imported on a one per one basis as version numbers are no longer "batched" and we don't have a good method for incremental automated BOM generation yet.  If you are using our development POMs, you can do an automatic version update which will keep the properties in sync with the latest [see version updates](version-updates.md#update-all-library-versions-and-parent-dependencies).

```xml
<dependencyManagement>
    <dependency>
        <groupId>com.limemojito.oss.standards</groupId>
        <artifactId>library</artifactId>
        <version>${oss-maven-standards-library.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```
