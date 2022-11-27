package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Implementation of Playfair.  Includes support for <code>5x5</code> grid with either J or Q
 * as the dummy, <code>6x6</code> grid, or <code>7x4</code> grid.
 * @author Sean A. Irvine
 */
class Playfair {

  // For details on the 5x5 and 6x6 versions see Wikipedia.
  //
  // The 7x4 variant is described in:
  // A. Aftab Alam, B. Shah Khalid, C. Muhammad Salam,
  // "A Modified Version of Playfair Cipher Using 7x4 Matrix",
  // Int. J of Computer Theory and Engineering, 5(4), 626--628, 2013.

  static final String FIVE_ALPHABET_NO_J = "ABCDEFGHIKLMNOPQRSTUVWXYZ";
  private static final String FIVE_ALPHABET_NO_Q = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
  static final String SIX_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  static final String SEVEN_BY_FOUR_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ*#";
  static final String EIGHT_BY_EIGHT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!*@-_$#,/+?;%=&'\\)[]:<(>\"{}";

  private static char[] buildKey(final String key, final String alphabet, final int gridWidth, final boolean noQ) {
    final HashSet<Character> seen = new HashSet<>();
    final char[] grid = new char[alphabet.length()]; // To be interpreted as width x height later
    // Use characters appearing in key, but care is needed with repeats and I=J (or similar)
    int j = 0;
    for (int k = 0; k < key.length(); ++k) {
      char c = Character.toUpperCase(key.charAt(k));
      if (gridWidth == 5) {
        if (noQ) {
          if (c == 'Q') {
            continue; // Skip Q
          }
        } else {
          if (c == 'J') {
            c = 'I';
          }
        }
      }
      if (alphabet.indexOf(c) >= 0 && seen.add(c)) {
        grid[j++] = c;
      }
    }
    for (int k = 0; k < alphabet.length(); ++k) {
      final char c = alphabet.charAt(k);
      if (seen.add(c)) {
        grid[j++] = c;
      }
    }
    assert (gridWidth == 5 && j == 25) || (gridWidth == 6 && j == 36) || (gridWidth == 4 && j == 28);
    return grid;
  }

  private final String mAlphabet;
  private final String mKey;
  private final char mDummy;
  private final char mPadding;
  private final int mGridWidth;
  private final int mGridHeight;

  Playfair(final String key, final int gridRows, final int gridCols, final char dummy, final char padding, final boolean noQ, final boolean scd) {
    mGridWidth = gridCols;
    mGridHeight = gridRows;
    mDummy = dummy;
    mPadding = padding;
    switch (gridCols) {
      case 4:
        mAlphabet = SEVEN_BY_FOUR_ALPHABET;
        break;
      case 5:
        mAlphabet = noQ ? FIVE_ALPHABET_NO_Q : FIVE_ALPHABET_NO_J;
        break;
      case 6:
        mAlphabet = SIX_ALPHABET;
        break;
      case 8:
        mAlphabet = EIGHT_BY_EIGHT_ALPHABET;
        break;
      default:
        throw new RuntimeException();
    }
    mKey = new String(scd ? ShrivastavaChouhanDhawan.buildKey(key, mAlphabet, gridCols) : buildKey(key, mAlphabet, gridCols, noQ));
  }

  private char getNextCharToEncode(final BufferedReader r) throws IOException {
    int c;
    while ((c = r.read()) >= 0) {
      final char d = Character.toUpperCase((char) c);
      if (mAlphabet.indexOf(d) >= 0) {
        return d;
      } else if (d == 'J') {
        return 'I';
      }
    }
    return 0; // No more characters
  }

  /**
   * Get the internal key.
   * @return the key
   */
  public String getKey() {
    return mKey;
  }

  char[] transform(final char a, final char b, final boolean encode) {
    assert a != b;
    final int deltaW = encode ? 1 : mGridWidth - 1; // i.e. +1, -1 mod mGridWidth
    //final int deltaH = encode ? 1 : mGridHeight - 1; // i.e. +1, -1 mod mGridHeight
    final int za = mKey.indexOf(a);
    final int zb = mKey.indexOf(b);
    assert za >= 0;
    assert zb >= 0;
    final int xa = za % mGridWidth;
    final int ya = za / mGridWidth;
    final int xb = zb % mGridWidth;
    final int yb = zb / mGridWidth;
    assert ya < mGridHeight && yb < mGridHeight;
    // (xa,ya) and (xb,yb) are now the corners of the rectangle in the key grid
    final int nxa;
    final int nya;
    final int nxb;
    final int nyb;
    if (ya == yb) { // same row
      nya = ya;
      nyb = yb;
      nxa = (xa + deltaW) % mGridWidth;
      nxb = (xb + deltaW) % mGridWidth;
    } else if (xa == xb) { // same col
      nxa = xa;
      nxb = xb;
      nya = (ya + deltaW) % mGridHeight;
      nyb = (yb + deltaW) % mGridHeight;
    } else { // rectangle
      nya = ya;
      nxa = xb;
      nyb = yb;
      nxb = xa;
    }
    final int nza = mGridWidth * nya + nxa;
    final int nzb = mGridWidth * nyb + nxb;
    return new char[] {mKey.charAt(nza), mKey.charAt(nzb)};
  }

  String transform(final BufferedReader r, final boolean encode) throws IOException {
    final StringBuilder sb = new StringBuilder();
    char prev = 0; // Remembers any previous character not yet encoded
    while (true) {
      // Attempt to get a pair of characters (a,b) to encode.  We need a != b.
      final char a;
      if (prev != 0) {
        a = prev;
        prev = 0;
      } else {
        a = getNextCharToEncode(r);
        if (a == 0) {
          break; // We have reached EOF
        }
      }
      final char b = getNextCharToEncode(r);
      if (a == b) {
        // Playfair cannot handle this, we need to use a dummy and stash this b for later
        if (a == mDummy) {
          throw new IllegalArgumentException("Input contains consecutive dummy (" + mDummy + ") characters, choose a different dummy");
        }
        prev = b;
        sb.append(transform(a, mDummy, encode));
      } else if (b == 0) {
        // We are left with "a" at EOF, use a dummy for "b"
        sb.append(transform(a, mPadding, encode));
        break; // EOF
      } else {
        sb.append(transform(a, b, encode));
      }
    }
    String transform = sb.toString();
    if (!encode && mGridWidth == 4) {
      // Strip out dummies
      transform = transform.replaceAll("[*#]", "");
    }
    return transform;
  }
}
