# maven-basics

## 1. Starting with a minimal POM

The POM is written in a XML file. Its content and structure are described by the Maven XSD (XML Schema Definition). Since Maven 2, the version 4 of the Maven XSD is used, it is available here: https://maven.apache.org/xsd/maven-4.0.0.xsd.

Therefore, the pom.xml should start this way:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
</project>
```
The XML prolog has been added for good measure but it is optional in XML 1.0. For more information on the attributes of the project tag, check https://stackoverflow.com/q/34202967.

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

Running `mvn install` will fail with the error message: _Source option 5 is no longer supported. Use 7 or later._ The reason is that with Maven 3.8.6, the version 3.1 of maven-compiler-plugin is used, and the plugin has a default value for its `source` and `target` flags set to 1.5 until its version 3.8.0 (exclusive). This can be verified here: https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#source.

To fix the error, we should specify the Java version used in the project. This can be done by specifying the Java version in the `source` and `target` flags of the compiler plugin (or by specifying only the `release` flag, for Java 9+). The flags can be specified in the plugin configuration or with a user property (e.g. `maven.compiler.release`).

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

By adding the plugin in build.pluginManagement and not in build.plugins, we show that the binding is already done by default by Maven.

Indeed, adding a plugin in build.pluginManagement does not bind any of its goals to a lifecycle phase. For the binding to be done, the plugin must be explicitly added to the `build.plugins` section. As shown, this is not needed for this plugin and a few others.

See: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle-bindings-packaging-ejb-ejb3-jar-par-rar-war

## 3. Specifying the default encoding

As each platform can have its own encoding defined, we should tell Maven which encoding it should use, so as not to make the build platform dependent, as mentioned in the following warning:

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

However, we usually set a value for the `project.build.sourceEncoding` property which is the default for the `encoding` flag of both the compiler plugin and the resources plugin, and surely other plugins too.

```xml
<properties>
    …
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

See: https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#encoding

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

By default, Maven binds a plugin goal to each of the phase mentioned above, see: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle-bindings-packaging-ejb-ejb3-jar-par-rar-war. To prevent the goal from running, we can bind it to an nonexistent phase:

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

This profle is invoked with the following command:

```sh
mvn install -Pfast
```

## 7. Using plugins directly

Maven is all about plugins. To demonstrate this, we can make a change in the source code and then call the maven-compiler-plugin and maven-jar-plugin directly, and see that the code change will have been taken into account:

```sh
mvn compiler:compile
mvn jar:jar
java -jar target/maven-basics-1.0.0-SNAPSHOT.jar
```

