#!/bin/bash

filename="$1"
code=""
newline=$'\n'

while read -r line; do
    name="$line"
    code="$code $newline $name"
done < "$filename"

#echo $code

cd /usr/local/bin
javac shBox.java
java ShBox "$code"
cd -


