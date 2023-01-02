# maven-basics

report for surefire and report for dependencies

## 1. Transaction domain object

The `Transaction` domain object was created in order to help create a meaningful unit test. It only has a quantity and a status, as well as three methods that return a new transaction with an updated status.

The `App` class provides three public methods to create, validate and execute a transaction. It is these methods that will be tested with unit tests, not the domain object directly. This class acts as a simple client of the `Transaction` domain object.

## 2. Unit tests

### JUnit & Assertj

The unit test is written in the `AppTest` class in `src/test/java` folder. It uses the JUnit framework as well as the Assertj library (optional).

Documentation:

* https://junit.org/junit5/docs/current/user-guide/
* https://assertj.github.io/doc/

The JUnit and Assertj dependencies are added with the `test` scope in `dependencyManagement` and then used in `dependencies`. The test scope indicates that the dependencies will only be available during the test compilation and test execution phases.

### maven-surefire-plugin

In order for the JUnit v5 tests to be detected, the maven-surefire-plugin needs to be upddated as the version 2.12.4 does not support JUnit 5. The version is added `pluginManagement` section.

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M7</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

Documentation:

* https://maven.apache.org/surefire/maven-surefire-plugin/index.html

#### Running tests

Tests can be run with the following commands:

```sh
mvn test
mvn surefire:test
mvn surefire:test -Dtest=edu.self.nyg.maven.basics.AppTest#testCreateTransaction

# for help on available options
mvn surefire:help -Ddetail=true -Dgoal=test
```

## 3. Third-party dependencies

### SLF4J

* https://www.slf4j.org/manual.html

SLF4J is a facade for logging frameworks, it provides an API and the actual logging framework can be provided at runtime. Here, we use the java.util.logging (JUL) logger.

```xml
<!-- SLF4J API -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.6</version>
</dependency>
<!-- Logging provider: java.util.logging, JDK 1.4+ -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>2.0.6</version>
    <scope>runtime</scope>
</dependency>
```

In order to use the logger in a class, we use the Lombok `@Slf4j` annotation which generates the following code:

```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(App.class);
```

### Lombok

* https://projectlombok.org/

Lombok is library that avoids having to writter getter, setters, constructors, etc. by generating them a compile time. For this reason, the dependency has scope `provided` (it is actually not provided anywhere, but as it is not used at runtime, it will not cause any issue).

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
    <scope>provided</scope>
</dependency>
```

## 4. Dependecy analysis

```sh
$ mvn org.apache.maven.plugins:maven-dependency-plugin:3.4.0:tree -Dverbose
edu.self.nyg:maven-basics:jar:1.0.0-SNAPSHOT
+- org.projectlombok:lombok:jar:1.18.24:provided
+- org.slf4j:slf4j-api:jar:2.0.6:compile
+- org.slf4j:slf4j-jdk14:jar:2.0.6:runtime
|  \- (org.slf4j:slf4j-api:jar:2.0.6:runtime - version managed from 2.0.6; omitted for duplicate)
+- org.junit.jupiter:junit-jupiter:jar:5.9.1:test
|  +- org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test
|  |  +- org.opentest4j:opentest4j:jar:1.2.0:test
|  |  +- org.junit.platform:junit-platform-commons:jar:1.9.1:test
|  |  |  \- (org.apiguardian:apiguardian-api:jar:1.1.2:test - omitted for duplicate)
|  |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
|  +- org.junit.jupiter:junit-jupiter-params:jar:5.9.1:test
|  |  +- (org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test - omitted for duplicate)
|  |  \- (org.apiguardian:apiguardian-api:jar:1.1.2:test - omitted for duplicate)
|  \- org.junit.jupiter:junit-jupiter-engine:jar:5.9.1:test
|     +- org.junit.platform:junit-platform-engine:jar:1.9.1:test
|     |  +- (org.opentest4j:opentest4j:jar:1.2.0:test - omitted for duplicate)
|     |  +- (org.junit.platform:junit-platform-commons:jar:1.9.1:test - omitted for duplicate)
|     |  \- (org.apiguardian:apiguardian-api:jar:1.1.2:test - omitted for duplicate)
|     +- (org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test - omitted for duplicate)
|     \- (org.apiguardian:apiguardian-api:jar:1.1.2:test - omitted for duplicate)
\- org.assertj:assertj-core:jar:3.23.1:test
   \- net.bytebuddy:byte-buddy:jar:1.12.10:test
```

```sh
$ mvn org.apache.maven.plugins:maven-dependency-plugin:3.4.0:list                 
The following files have been resolved:
   org.projectlombok:lombok:jar:1.18.24:provided -- module lombok
   org.slf4j:slf4j-api:jar:2.0.6:compile -- module org.slf4j
   org.slf4j:slf4j-jdk14:jar:2.0.6:runtime -- module org.slf4j.jul
   org.junit.jupiter:junit-jupiter:jar:5.9.1:test -- module org.junit.jupiter
   org.junit.jupiter:junit-jupiter-api:jar:5.9.1:test -- module org.junit.jupiter.api
   org.opentest4j:opentest4j:jar:1.2.0:test -- module org.opentest4j
   org.junit.platform:junit-platform-commons:jar:1.9.1:test -- module org.junit.platform.commons
   org.apiguardian:apiguardian-api:jar:1.1.2:test -- module org.apiguardian.api
   org.junit.jupiter:junit-jupiter-params:jar:5.9.1:test -- module org.junit.jupiter.params
   org.junit.jupiter:junit-jupiter-engine:jar:5.9.1:test -- module org.junit.jupiter.engine
   org.junit.platform:junit-platform-engine:jar:1.9.1:test -- module org.junit.platform.engine
   org.assertj:assertj-core:jar:3.23.1:test -- module org.assertj.core
   net.bytebuddy:byte-buddy:jar:1.12.10:test -- module net.bytebuddy
```
