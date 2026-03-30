### 17.0.2

ce83168 - [Release] updated version in pom.xml
93c71d7 - #104 Version update on a cron basis.
aafb94e - Merge remote-tracking branch 'origin/master'
cf6861a - Merge pull request #106 from LimeMojito/feature/104
4bf2668 - #104 Release always on main or master push.
207f6b3 - #104 Remove git setup and checkout for release build as already in environment.  Add version update to release steps.  Reduce inputs in action call.
db53023 - Updated library versions in pom.xml
ea4af9e - #104 Added explicit dependency management for lombok.  Corrected to real versions.
4b94817 - Merge branch 'master' into feature/104
4c3b66b - #104 Force a library update to test skipping Lime-Build-Agent commits.
9e77064 - #104 Echo some feedback in actions.  Do not trigger feature build if Lime-Build-Agent has triggered it (via a commit for example).
345bcf7 - Updated library versions in pom.xml
6fa1576 - #104 Test version update requiring commit.
747e107 - Merge branch 'master' into feature/104
6677731 - #104 Factor out individual setup actions for docker and git.  Setup keys for git commits.  Remove old build action.
183d5a1 - #104 Update description of environment setup java.
dca2141 - Merge pull request #105 from LimeMojito/feature/104
348d9db - #104 Push checkout to front of build.
4b6722b - #104 Adjust build name.
df6e616 - #104 Add a version updating action for feature builds.  All feature branches will now build on push, and update maven library versions.
28ba655 - [Development] updated development version in pom.xml

