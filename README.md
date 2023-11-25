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

Similar modules are available for solving Playfair and Vigenere ciphers.

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

The similar `ladder` module can be used to solve related problems where
the words get shorter or longer by one letter at each step.

## Explain word sequences

The `wordsmith` module applies a collection of tests in an attempt to
discover an explanation for a sequence of words.

```
> jilt wordsmith Vienna Brussels Prague Copenhagen Tallinn Helsinki Athens Budapest
All words are cities
All words are capital cities
Associated words are in alphabetical order:
[Austria, Belgium, Czech Republic, Denmark, Estonia, Finland, Greece, Hungary]

> jilt wordsmith NIGHT, PAINTING, INIQUITY, THRIVE, VAPOURS, VERIFY, REVISIT
Increasing Roman numerals:
[I, II, III, IV, V, VI, VII]
```

## Wordle

Wordle is a popular word game developed in 2022 by Josh Wardle, see
[here](https://www.nytimes.com/games/wordle/index.html)

The player has to guess a five letter word and the responses give clues
as to correct letters and wrong letters. The JILT module `wordle` can
be used to get suggestions as to what should be played as the next
guesses given the state of any previous guesses.

At the start we have made no guesses, so:

```
> jilt wordle
Remaining words: 12478
Best: TARES -4.30
```

The above says in the absence of any information and using the default
dictionary, the best word to guess is `TARES`. If you have access to the
actual Wordle dictionary, you can specify that instead with `-D`.

In this example, when we play `TARES` we get told that `A` and `R` are
correct letters but in the wrong positions and that the other letters
do not occur. We use 1 to indicate a correct letter and 0 for a wrong
letter:

```
> jilt wordle TARES 01100
Remaining words: 240
Best: GRAIL -3.17
```

We are down to 240 remaining possible words. In our, example we learn
that `A` is now in correct position, but `R` is still in the wrong
position, and none of the other new letters occur. So, using 2 to
indicate a correctly placed letter:

```
> jilt wordle TARES 01100 GRAIL 01200
Remaining words: 24
Best: HOARD -2.28
```

We play `HOARD` and discover that `ARD` is correct and that `H` also
occurs in the word (which actually means `H` must be the second letter).
We continue:

```
> jilt wordle TARES 01100 GRAIL 01200 HOARD 10222
```

There is a single remaining word `CHARD`, which is indeed correct.

The `wordle` module supports other variants of the game where multiple
words are simultaneously solved for (dordle, qordle, etc.) or where
the word length is something other than 5.

## Other

There are other modules for finding words in a grid (wordsearch),
generating permutations of letters, transforming words in various ways
and so on.

## Miscellaneous examples

Find all words for which after reversing the letters remain a word:

```
> jilt dict | jilt transform --reverse | jilt filter --in-dict
```
