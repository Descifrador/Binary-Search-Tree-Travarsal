BASEDIR=$(CURDIR)
OUTPUTDIR=$(BASEDIR)/output
PACKAGE=$(BASEDIR)/src/main/java

html:
	javadoc -sourcepath "$(PACKAGE)" -d "$(OUTPUTDIR)" -encoding UTF-8 -subpackages .

.PHONY: html