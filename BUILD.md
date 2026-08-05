# Building NeuPaths

NeuPaths is quite simple to build, even if your environment does not support `gmake` or `bash`.  All you really need is JDK version 17 or newer.

## Using makefiles

The repository contains makefiles.  If your environment supports `gmake`, then running `make all` in the top-level directory will build the NeuPaths JAR, all of the examples and the javadocs.

## Using shell commands

If your environment does not support `gmake` but does support a modern shell (e.g. bash or ksh), you can use the commands in the `source/java/Makefile` to build the NeuPaths JAR.  Then you can use the commands in the `documents/Makefile` to generate the Javadocs.  Finally, you can look at the README.md file in each example subdirectory for instructions on building the example.

## Manually invoking the compiler

To build the NeuPaths JAR manually, do the following:

1. Navigate to the `source/java/neupaths/api` directory.

2. Execute: `javac -cp ../.. -g -Xdiags:verbose *.java`

3. Navigate to the `source/java/neupaths/stim` directory.

4. Execute: `javac -cp ../.. -g -Xdiags:verbose *.java`

5. Navigate to the `source/java/neupaths/util` directory.

6. Execute: `javac -cp ../.. -g -Xdiags:verbose *.java`

7. Return to the `source/java` directory.

8. Create a text file that lists all of the .class files with paths relative to the `source/java` directory (ex: `neupaths/util/IssueCommandToDaemon.class`)

9. Execute: `jar cvf neupaths.jar @class.list`

