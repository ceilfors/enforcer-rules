enforcer-rules
==============

Additional custom rules for Maven enforcer.

The extra rule that are currently available:
* requireDepMgt - verifies if dependency management is used

### RequireDepMgt ###
Supported parameters:
* checkVersion - if true, this rule will fail if the version managed by dependencyManagement is being overridden. Overridding a dependency version with the same version declared in the dependency management won't fail the rule. Default is **false**.
* ignoreArtifacts - a list of artifacts to be ignored. Format of the artifact is [groupId]:[artifactId]:[packaging]
* ignoreScopes - a list of scopes to be ignored. Default is ignoring **test** scope

Sample plugin configuration:
```
<project>
  [...]
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0</version>
        <executions>
          <execution>
            <id>enforce</id>
            <goals>
              <goal>enforce</goal>
            </goals>
            <configuration>
              <rules>
                <requireDepMgt>
                   <checkVersion>true</checkVersion>
                   <!-- example of ignoring an artifact -->
                   <ignoreArtifacts>
                      <ignoreArtifact>commons-io:commons-io:jar</ignoreArtifact>
                   </ignoreArtifacts>
                   <!-- example of ignoring runtime scope -->
                   <ignoreScopes>
                      <ignoreScope>runtime</ignoreScope>
                   </ignoreScopes>
                </requireDepMgt>
              </rules>
            </configuration>
          </execution>
        </executions>
        <dependencies>
          <dependency>
            <groupId>com.ceilfors.maven.plugin</groupId>
            <artifactId>enforcer-rules</artifactId>
            <version>1.0</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>
  [...]
</project>
```