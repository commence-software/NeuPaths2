#!/bin/bash

rm run.out
java -cp ${NEUPATHS_HOME}/neupaths.jar:. Main 2>&1 | tee run.out

