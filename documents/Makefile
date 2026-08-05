all:
	javadoc -d javadoc -sourcepath ../source/java -protected -subpackages neupaths -doctitle "NeuPaths SDK" -Xdoclint:none -overview ${NEUPATHS_HOME}/overview.html -header "Copyright &copy; 2024 Commence Software LLC" 2>&1 | tee javadoc.out

clean:
	rm -rf javadoc/
