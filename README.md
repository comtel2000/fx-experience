# fx-onscreen-keyboard
[![Build Status](https://travis-ci.org/comtel2000/fx-experience.png?branch=master)](https://travis-ci.org/comtel2000/fx-experience)  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.comtel2000/fx-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.comtel2000/fx-parent)  [![License](https://img.shields.io/badge/license-BSD--3--Clause-blue.svg)](http://opensource.org/licenses/BSD-3-Clause)

*FXOK* provides a JavaFX based virtual on-screen keyboard component for touch based monitors with xml layout configuration

## Features
* support multiple key button layouts (numeric, symbol, text, shift)
* free movable with auto positioning
* select all, copy, paste, cut buttons
* resizable by zoom in/out button
* dynamic text node property definition ('vkType' and 'vkLocale')
* context popup with additional chars
* css style

## Modules
* fx-onscreen-keyboard (fx components)
* fx-onscreen-keyboard-swing (swing components)
* fx-onscreen-keyboard-samples (sample package)

![num block ctrl] (doc/num_block.png)

![num block] (doc/num_block_ctrl.png)

![layout ru] (doc/layout_ru.png)

[![video](http://img.youtube.com/vi/CD9lS_HZ4fA/0.jpg)](http://youtu.be/CD9lS_HZ4fA)

## How to build and run sample
What is required:

* Latest stable [Oracle JDK 8](http://www.oracle.com/technetwork/java)
* Latest stable [Apache Maven](http://maven.apache.org)

```shell
mvn clean install
java -jar fx-onscreen-keyboard-samples/target/fx-onscreen-keyboard-jar-with-dependencies.jar
```

## Maven central repository

```xml
<dependency>
  <groupId>org.comtel2000</groupId>
  <artifactId>fx-onscreen-keyboard</artifactId>
  <version>[LATEST_RELEASE]</version>
</dependency>
```

## License
[BSD 3-Clause License](http://opensource.org/licenses/BSD-3-Clause)
