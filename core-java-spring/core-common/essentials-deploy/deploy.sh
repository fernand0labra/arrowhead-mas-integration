#!/bin/bash

cp ../target/arrowhead-core-common-essentials-java-spring.jar .

mv arrowhead-core-common-essentials-java-spring.jar arrowhead-core-common-essentials-java-spring-4.4.0.jar

mvn install:install-file -DpomFile=arrowhead-core-common-essentials-java-spring-4.4.0.pom -Dfile=arrowhead-core-common-essentials-java-spring-4.4.0.jar

rm -rf arrowhead-core-common-essentials-java-spring-4.4.0.jar