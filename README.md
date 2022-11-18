# JILT (Java Interactive Language Tools)

JILT provides a set of utility functions for assisting with the
solution of various word based puzzles and problems.

Unless you want to directly work with the code it is recommended that
you download the latest release from
[here](https://github.com/archmageirvine/jilt/releases).

JILT can perform a variety of functions like solving simple
substitution ciphers, finding anagrams, palindromes, tautonyms, and
other word curios.

## Solving simple substitution ciphers

The `vampire` module can be used to solve a variety of simple
substitution cipher problems, including short examples which are
difficult to solve by hand. Various options are available to show
progress and control how much effort is expended on finding a good
solution. The default model is suitable for English language ciphers.

Example:

```
> cat cryptogram.txt
RA PJIA JS C RUSGADLBP RUDPG FXCF JE LBPP UL ZACBFK, NXCDT CSG
CGIASFBDA. FXADA JE SU ASG FU FXA CGIASFBDAE FXCF RA NCS XCIA JL USPK
RA EAAV FXAT RJFX UBD AKAE UOAS. (MCRCXCDPCP SAXDB)

> jilt vampire -q -r 1 -i cryptogram.txt
274.068 we live in a wonderful world that is full of beauty charm and adventure there is no end to the adventures that we can have if only we seek them with our eyes open jawaharlal nehru
```

Often cryptograms will be presented with spaces removed. In that
situation it is better to use a corresponding model:

```
> cat cryptogram.txt
RAPJI AJSCR USGAD LBPRU DPGFX CFJEL BPPUL ZACBF K,NXC DTCSG CGIAS
FBDAF XADAJ ESUAS GFUFX ACGIA SFBDA EFXCF RANCS XCIAJ LUSPK RAEAA
VFXAT RJFXU BDAKA EUOAS MCRCX CDPCP SAXDB

> jilt vampire -q -r 1 --ignore-spaces -m models/nospace.model -i ~/cryptogram.txt
21.098 weliveinawonderfulworldthatisfullofjeautycharmandadventurethereisnoendtotheadventuresthatwecanhaveifonlyweseekthemwithoureyesogenbawaharlalnehru
```

Note we have used the `--ignore-spaces` option here because the spaces
in the cryptogram file are not properly a part of the message.  Note
also that in this case, the correct solution is not the "best"
solution found.

A similar module is available for solving Vigenere ciphers.

## Finding anagrams

To find anagrams of a word:

```
> jilt anagram cat
act
cat
```

There are options available to find multiple word anagrams, override
the word list, support unknown letters in the anagram, and so on.

## Finding words with particular properties

The filter module provides a great many options for selecting words
with various properties. In general, a word must pass all the selected
filters to be output.

Here we find all the palindromes of length 7 in JILT's standard dictionary:

```
> jilt dict | jilt filter --palindrome --length 7
deified
hadedah
halalah
reifier
repaper
reviver
rotator
seities
sememes
```

## Solve letter equations

Currently the support for this kind of problem is not as complete as
it could be. In these problems it is assumed that each letter denotes
a single digit and each digit is represented by only a particular
letter.

All solutions will be printed.

In the following example, there are two equations which much be satisfied.

```
> jilt equation "EAST + WEST + SOUTH + NORTH = EARTH" "W^N=WHEN"
A = 7, R = 8, S = 2, T = 5, E = 9, U = 3, W = 4, H = 0, N = 6, O = 1.
```

Note the use of quotes to ensure each equation is seen as a separate parameter.

## Solve word chain problems

A word chain problem asks for the shortest sequence of words that can
transform one word into another. Options are available to select the
exact set of allowed operations.

```
> jilt chain horse truck
[horse, hoise, poise, prise, price, trice, truce, truck]
```

## Other

There are other modules for finding words in a grid (wordsearch),
generating permutations of letters, transforming words in various ways
and so.
