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
RA PJIA JS C RUSGADLBP RUDPG FXCF JE LBPP UL ZACBFK, NXCDT CSG CGIASFBDA. FXADA JE SU ASG FU FXA CGIASFBDAE FXCF RA NCS XCIA JL USPK RA EAAV FXAT RJFX UBD AKAE UOAS. (MCRCXCDPCP SAXDB)

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


