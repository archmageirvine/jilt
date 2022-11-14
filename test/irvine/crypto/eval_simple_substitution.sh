#!/bin/bash

for c in cryptograms/*.txt; do
    answer=$(java irvine.crypto.Vampire -q -r 1 ${c} | sed 's/^[0-9.]* //')
    expected=$(tr '[:upper:]' '[:lower:]' <${c} | tr '/-' '  ' | tr -d -c "a-z 0")
    echo ${c} $(java irvine.crypto.SimpleCryptoCompare "$answer" "$expected")
done
