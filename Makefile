BASEDIR=$(CURDIR)
OUTPUTDIR=$(BASEDIR)/build/docs/javadocs
PACKAGE=$(BASEDIR)/src/main/java

html:
	chmod +x gradlew
	./gradlew javadoc

.PHONY: html