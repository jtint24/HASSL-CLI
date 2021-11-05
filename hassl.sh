#!/bin/bash

filename="$1"
code=""
newline=$'\n'

while IFS= read -r line || [[ -n "$line" ]]; do
    name="$line"
    code="$code $newline $name"
done < "$filename"



#echo $code

cd /usr/local/bin
javac shBox.java
java ShBox "$code"
cd -

