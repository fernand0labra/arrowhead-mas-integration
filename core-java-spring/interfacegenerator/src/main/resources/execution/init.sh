	#!/bin/bash
	
	cd ./src/main/java/generatedinterface

	mvn install 

	echo on

	java -jar ./target/generatedinterface-1.0.jar