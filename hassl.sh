#!/bin/bash
function hassl () {
     cd /usr/local/bin
     java shBox.java $1
     cd -
}

hassl
