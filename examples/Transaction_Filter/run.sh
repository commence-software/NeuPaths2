#!/bin/bash

rm run.out

java -cp ../../source/java/neupaths.jar:. Main 2>&1 | tee run.out

