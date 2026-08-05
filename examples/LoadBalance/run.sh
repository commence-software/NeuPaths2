#!/bin/bash

rm -f /tmp/*.sock
rm -f /tmp/*.syn
rm -f *.out
java -cp ${NEUPATHS_HOME}/neupaths.jar:. Main 2>&1 | tee run.out

