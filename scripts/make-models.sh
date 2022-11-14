#!/bin/bash
#
# Build entropy models.  Note this assumes a significant corpus which
# is not part of the JILT repository.
# @author Sean A. Irvine

THIS_DIR=$(dirname "$0")
MODEL_DIR=${THIS_DIR}/../models
TEST="the quick brown fox jumps over the lazy dog"
CORPUS=~/Corpus
SRC=(${CORPUS}/2*/* ${CORPUS}/reuters/*.gz ${CORPUS}/misc/*.gz ${CORPUS}/wikipedia/enwiki*.gz)

# Training corpus consists of text of Wikipedia, a bunch of novels, and sample
# of Reuters newswire, and various other things.  Note that due to scaling
# during model building, the influence of earlier text is reduced over time.

# 31 letter English model: A-Z, space, digit, quote, punctuation, other
model="${MODEL_DIR}/default.model"
if [[ ! -r ${model} ]]; then
    echo "Building ${model}"
    zcat -f "${SRC[@]}" | tr '/-' ' ' | tr -dc "A-Za-z0-9,.';: \n" | java irvine.entropy.ReducedAlphabet | java irvine.entropy.FourGramAlphabetModel --build -m "${model}" --alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ 0.'|" -u -
    echo "${TEST}" | java irvine.entropy.FourGramAlphabetModel -V -u -m "${model}"
fi

# 26 letter English model: A-Z
model="${MODEL_DIR}/nospace.model"
if [[ ! -r ${model} ]]; then
    echo "Building ${model}"
    zcat -f "${SRC[@]}" | tr -dc "A-Za-z" | java irvine.entropy.ReducedAlphabet | java irvine.entropy.FourGramAlphabetModel --build -m "${model}" --alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ" -u -
    echo "${TEST}" | java irvine.entropy.FourGramAlphabetModel -V -u -m "${model}"
fi

# 5x5 Playfair model, identifies I and J together, X is the dummy
model="${MODEL_DIR}/playfair5.model"
if [[ ! -r ${model} ]]; then
    echo "Building ${model}"
    zcat -f "${SRC[@]}" | tr '[:lower:]' '[:upper:]' | tr J I | tr -dc '[:upper:]' | java irvine.crypto.InsertPlayfairDummies | java irvine.entropy.FourGramAlphabetModel --build -m "${model}" --alphabet ABCDEFGHIKLMNOPQRSTUVWXYZ -u -
    echo "${TEST}" | java irvine.entropy.FourGramAlphabetModel -V -u -m "${model}"
fi

# 6x6 Playfair model, letters and digits, assumes that X is used as the dummy.
model="${MODEL_DIR}/playfair6.model"
if [[ ! -r ${model} ]]; then
    echo "Building ${model}"
    zcat -f "${SRC[@]}" | tr '[:lower:]' '[:upper:]' | tr -dc 'A-Z0-9' | java irvine.crypto.InsertPlayfairDummies | java irvine.entropy.FourGramAlphabetModel --build -m "${model}" --alphabet ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 -u -
    echo "${TEST}" | java irvine.entropy.FourGramAlphabetModel -V -u -m "${model}"
fi

# 7x4 Playfair model, letters and "*" used as the dummy, "#" as padding.
model="${MODEL_DIR}/playfair7x4.model"
if [[ ! -r ${model} ]]; then
    echo "Building ${model}"
    zcat -f "${SRC[@]}" | tr '[:lower:]' '[:upper:]' | tr -dc '[:upper:]' | java irvine.crypto.InsertPlayfairDummies '*' | java irvine.entropy.FourGramAlphabetModel --build -m "${model}" --alphabet 'ABCDEFGHIJKLMNOPQRSTUVWXYZ*#' -u -
    echo "${TEST}" | java irvine.entropy.FourGramAlphabetModel -V -u -m "${model}"
fi
