#!/bin/bash

rm -f /tmp/*.sock
rm -f *.out

java -cp ../../source/java/neupaths.jar:. Main 2>&1 | tee run.out

