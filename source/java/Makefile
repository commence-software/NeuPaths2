.PHONY: all clean

all:
	find neupaths -name "*.java" > source.list
	javac -g -Xdiags:verbose @source.list
	find neupaths -name "*.class" > class.list
	jar cvf neupaths.jar @class.list


clean:
	rm -f source.list
	rm -f class.list
	rm -f neupaths.jar
	find neupaths -name "*.class" -exec rm {} \;
