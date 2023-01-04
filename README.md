# maven-basics

## 1. Starting with a minimal POM

The POM is written in an XML file. Its content and structure are described by the Maven XSD (XML Schema Definition). Since Maven 2, the version 4 of the Maven XSD is used, it is available here: https://maven.apache.org/xsd/maven-4.0.0.xsd.

Therefore, the pom.xml should start this way:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
</project>
```
The XML prolog has been added for good measure but it is optional in XML 1.0. For more information on the attributes of the project tag, read: https://stackoverflow.com/q/34202967.

The above XML is technically valid, however, in order to run any Maven command, the following tags are required: `modelVersion`, `groupId`, `artifactId`, `version`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>edu.self.nyg</groupId>
    <artifactId>maven-basics</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</project>
```

## 2. Setting the Java version

Running `mvn install` will fail with the error message: _Source option 5 is no longer supported. Use 7 or later._ The reason is that with Maven 3.8.6, the version 3.1 of maven-compiler-plugin is used, and the plugin has a default value for its `source` and `target` flags set to 1.5 until its version 3.8.0 (exclusive), as explained here: https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#source.

To fix this error, we should specify the Java version used in the project. This can be done by specifying the Java version in the `source` and `target` flags of the compiler plugin (or by specifying only the `release` flag, for Java 9+). The flags can be specified in the plugin configuration or with a user property (e.g. `maven.compiler.release`).

See: https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#release

Updating the compiler plugin is also an option, but specifying the Java version to be used in a project is a good practice. Here we do both:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>edu.self.nyg</groupId>
    <artifactId>maven-basics</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>17</maven.compiler.release>
    </properties>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.10.1</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

By adding the plugin only in `build.pluginManagement` and leaving `build.plugins` empty, we show that the binding is already done by default by Maven (_convention over configuration_).

Indeed, adding a plugin in `build.pluginManagement` does not bind any of its goals to a lifecycle phase. For the binding to be done, the plugin must explicitly be added to `build.plugins`. As shown, this is not needed for this plugin and a few others, see: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle-bindings-packaging-ejb-ejb3-jar-par-rar-war

## 3. Specifying the default encoding

As each platform can have its own default encoding, we should tell Maven which encoding it should use, so as not to make the build platform dependent, as mentioned in the following warning:

```
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ maven-basics ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
```

To fix that, we can specify a value for the `encoding` user property:

```xml
<properties>
    …
    <encoding>UTF-8</encoding>
</properties>
```

However, we usually set a value for the `project.build.sourceEncoding` property instead, which is the default value of the  `encoding` flag of both the compiler plugin and the resources plugin, and surely other plugins too, see: https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#encoding

```xml
<properties>
    …
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

## 4. Making the JAR executable

If we want to make the generated JAR executable, such as it can be invoked with `java -jar maven-basics-1.0.0.jar`, we need the `Main-Class` attribute to be defined in the MANIFEST.MF file of the JAR. As its name implies, this attribute lets the JAR know which class of our application it should execute.

The value can be configured through the maven-jar-plugin, which `jar` goal is bound to the `package` phase of the default lifecycle:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>edu.self.nyg.maven.basics.Main</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

Documentation: https://maven.apache.org/plugins/maven-jar-plugin/jar-mojo.html

## 5. Unbinding unused lifecycle phases

In this branch, our project has only one Java class, no resources and no tests. This means that the `process-resources`, `process-test-resources`, `test-compile` and `test` phases of the default lifecycle are not needed.

By default, Maven binds a plugin goal to each of the phases mentioned above, see: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle-bindings-packaging-ejb-ejb3-jar-par-rar-war. To prevent the goal from executing, we can bind it to an nonexistent phase:

```xml
<project>
    …
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-resources-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>default-resources</id>
                            <phase>none</phase> <!-- this phase doesn't exist -->
                        </execution>
                        <execution>
                            <id>default-testResources</id>
                            <phase>none</phase>
                        </execution>
                    </executions>
                </plugin>
            <plugins>
        <pluginManagement>
    <build>
<project>
```

To find the id of the execution that binds a plugin goal to a lifecycle phase, we can generate the effective POM using the maven-help-plugin:

```
mvn help:effective-pom -Dverbose | grep -B1 process-test-resources
```

## 6. Creating profiles

Going a bit further, we can keep the default build intact, i.e. not unbinding any plugin goals from lifecycle phases, and instead add a custom build profile that will contain the unbindings done previously:

```xml
<profiles>
    <profile>
        <id>fast</id>
        <build>
            <pluginManagement>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-resources-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>default-resources</id>
                                <phase>none</phase>
                            </execution>
                            <execution>
                                <id>default-testResources</id>
                                <phase>none</phase>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-compiler-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>default-testCompile</id>
                                <phase>none</phase>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>default-test</id>
                                <phase>none</phase>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </pluginManagement>
        </build>
    </profile>
</profiles>
```

The profle can be invoked with the following command:

```sh
mvn install -Pfast
```

## 7. Using plugins directly

Maven is all about plugins. To demonstrate this, we can make a change in the source code and then call the maven-compiler-plugin and maven-jar-plugin directly, and see that the code change will be reflected when executing the JAR:

```sh
mvn compiler:compile
mvn jar:jar
java -jar target/maven-basics-1.0.0-SNAPSHOT.jar
```

## 8. Generating a website

Until now we have use the clean and default lifecycle. The third one, the site lifecycle, is used to generate a website for the project.

If we try to run `mvn site` to generate the website, which executes the `site` goal of the maven-site-plugin, we get the following error:

```sh
java.lang.NoClassDefFoundError: org/apache/maven/doxia/siterenderer/DocumentContent
```

Basically, the version of maven-site-plugin is too old (3.3) and the version of maven-project-info-reports-plugin is too new (3.4.1). As we did for other plugins, we will explicitly specify the version for each plugin in the `pluginManagement` section.

For information on the error, see: [https://stackoverflow.com/a/51099913](https://stackoverflow.com/a/51099913)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <version>4.0.0-M3</version>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-project-info-reports-plugin</artifactId>
    <version>3.4.1</version>
</plugin>
```

Once the website has been generated, we can serve the website on [localhost:8080](http://localhost:8080/) with `mvn site:run` or we can simply open the index.html file in the `target/site` folder.

The website generated is very simple and contains information gathered from the pom.xml file. All pages are actually generated by the maven-project-info-reports-plugin. As we can site from the logs, 15 reports from this plugin are configured to run:

```sh
[INFO] Configuring report plugin org.apache.maven.plugins:maven-project-info-reports-plugin:3.4.1
[INFO] 15 reports detected for maven-project-info-reports-plugin:3.4.1: ci-management, dependencies, dependency-info, dependency-management, distribution-management, index, issue-management, licenses, mailing-lists, modules, plugin-management, plugins, scm, summary, team
```

However, due to the project having no dependencies nor modules, having a sparse pom.xml and no site.xml, only 5 reports actually produce a webpage. In the logs, we can see which goal procudes which page:

```sh
[INFO] Generating "Dependency Information" report --- maven-project-info-reports-plugin:3.4.1:dependency-info
[INFO] Generating "About" report         --- maven-project-info-reports-plugin:3.4.1:index
[INFO] Generating "Plugin Management" report --- maven-project-info-reports-plugin:3.4.1:plugin-management
[INFO] Generating "Plugins" report       --- maven-project-info-reports-plugin:3.4.1:plugins
[INFO] Generating "Summary" report       --- maven-project-info-reports-plugin:3.4.1:summary
```

Documentation for each goal can be found here: https://maven.apache.org/plugins/maven-project-info-reports-plugin/plugin-info.html.

In order to specify which reports should be run, a new section should be added in the pom.xml:

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-project-info-reports-plugin</artifactId>
            <reportSets>
                <reportSet>
                    <reports>
                        <report>index</report>
                    </reports>
                </reportSet>
            </reportSets>
        </plugin>
    <plugins>
<reporting>
```

With the above configuration, only the index report will run, generating the index.html page and its parent page project-info.html. If reports is left empty, no HTML page will be rendered.

Unfortunately, the documentation for each goal is sparse and it takes a bit of guessing/code reading to understand how to customize the output of each report. For example, the index report takes the project description from the `description` tag of the pom.xml.

Regarding the strings `Last Published: $dateValue` (top-left) and `Copyright © ${currentYear}` (bottom-right), they are missing template properties for the Velocity template of the maven-default-skin. This skin has not been updated since 2019 and might be replaced by the maven-fluido-skin, see: https://issues.apache.org/jira/browse/MSKINS-196. We can specify which skin to use in the site descriptor file (src/site/site.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/DECORATION/1.8.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/DECORATION/1.8.0 https://maven.apache.org/xsd/decoration-1.8.0.xsd"
         name="Maven Basics">

    <!-- necessary in order for the side bar menu to be generated -->
    <body>
        <menu ref="parent"/>
        <menu ref="modules"/>
        <menu ref="reports"/>
    </body>

    <skin>
        <groupId>org.apache.maven.skins</groupId>
        <artifactId>maven-fluido-skin</artifactId>
        <version>1.11.1</version>
    </skin>
</project>
```

For more information on the site descriptor file, see:

* https://maven.apache.org/plugins/maven-site-plugin/examples/sitedescriptor.html
* https://maven.apache.org/doxia/doxia-sitetools/doxia-decoration-model/decoration.html

For more information on the architecture of the maven-site-plugin, see:

* https://maven.apache.org/plugins/maven-site-plugin/history.html

For more information on available skins, see:

* https://maven.apache.org/skins/index.html

We can add an additional report in order to automatically generate Javadoc pages for the application code. For his, maven-javadoc-plugin should be added to the reporting section:

```xml
<reporting>
    <plugins>
        …
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.4.1</version>
        </plugin>
    </plugins>
</reporting>
```

Another report can be added to the `reporting` section:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>buildplan-maven-plugin</artifactId>
    <version>2.2.0</version>
</plugin>
```

```sh
mvn buildplan:list-phase -Dbuildplan.showLifecycles
mvn buildplan:list
mvn buildplan:list -DshowLifecycles
mvn buildplan:list -Dbuildplan.showLifecycles
mvn buildplan:list -Dbuildplan.showLifecycles -Pfast
mvn buildplan:list-phase
```

As well as:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.4.0</version>
</plugin>
```





* maven-site-plugin, https://github.com/apache/maven-site-plugin
  * arch: https://maven.apache.org/plugins/maven-site-plugin/history.html
  * maven-project-info-reports-plugin, https://github.com/apache/maven-project-info-reports-plugin
    * goal doc: https://maven.apache.org/plugins/maven-project-info-reports-plugin/plugin-info.html
  * velocity template
    * https://velocity.apache.org/
  * doxia
    * https://github.com/apache/maven-doxia-sitetools
    * https://maven.apache.org/doxia/doxia/
    * https://maven.apache.org/doxia/doxia-sitetools/doxia-site-renderer/index.html
  * skins
    * maven-default-skin, https://github.com/apache/maven-default-skin
      * https://issues.apache.org/jira/browse/MSKINS-196
    * maven-fluido-skin, https://github.com/apache/maven-fluido-skin
  * other reports
    * javadoc
    * examples of reports: https://maven.apache.org/plugins/maven-project-info-reports-plugin/project-reports.html

