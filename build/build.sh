#!/bin/bash

./gradlew -S --foreground -d cli -Pargs="$*"
