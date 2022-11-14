package irvine.crypto;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import irvine.util.CliFlags;

/**
 * Implementation of Playfair.  Includes support for <code>5x5</code> grid with either J or Q
 * as the dummy, <code>6x6</code> grid, or <code>7x4</code> grid.
 * @author Sean A. Irvine
 */
public class Playfair {

  // For details on the 5x5 and 6x6 versions see Wikipedia.
  //
  // The 7x4 variant is described in:
  // A. Aftab Alam, B. Shah Khalid, C. Muhammad Salam,
  // "A Modified Version of Playfair Cipher Using 7x4 Matrix",
  // Int. J of Computer Theory and Engineering, 5(4), 626--628, 2013.

  private static final String KEY_FLAG = "key";
  private static final String DECODE_FLAG = "decode";
  private static final String DUMMY_FLAG = "dummy";
  private static final String NO_Q_FLAG = "noq";
  private static final String SIX_FLAG = "6x6";
  private static final String SEVEN_BY_FOUR_FLAG = "7x4";
  private static final String EIGHT_FLAG = "8x8";
  private static final String SCD_FLAG = "scd";

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

  private char getNextCharToEncode(final InputStream is) throws IOException {
    int c;
    while ((c = is.read()) >= 0) {
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

  String transform(final InputStream is, final boolean encode) throws IOException {
    final StringBuilder sb = new StringBuilder();
    char prev = 0; // Remembers any previous character not yet encoded
    while (true) {
      // Attempt to get a pair of characters (a,b) to encode.  We need a != b.
      final char a;
      if (prev != 0) {
        a = prev;
        prev = 0;
      } else {
        a = getNextCharToEncode(is);
        if (a == 0) {
          break; // We have reached EOF
        }
      }
      final char b = getNextCharToEncode(is);
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

  private static final class PlayfairFlagsValidator implements CliFlags.Validator {

    @Override
    public boolean isValid(final CliFlags flags) {
      int c = 0;
      if (flags.isSet(SIX_FLAG)) {
        ++c;
      }
      if (flags.isSet(SEVEN_BY_FOUR_FLAG)) {
        ++c;
      }
      if (flags.isSet(EIGHT_FLAG)) {
        ++c;
      }
      if (c > 1) {
        flags.setParseMessage("At most one of --" + SIX_FLAG + ", --" + SEVEN_BY_FOUR_FLAG + ", and --" + EIGHT_FLAG + " can be set.");
        return false;
      }
      if (flags.isSet(DUMMY_FLAG) && flags.isSet(SEVEN_BY_FOUR_FLAG)) {
        flags.setParseMessage("There is no user selected dummy with 7x4.");
        return false;
      }
      if (flags.isSet(NO_Q_FLAG) && (flags.isSet(SEVEN_BY_FOUR_FLAG) || flags.isSet(SIX_FLAG))) {
        flags.setParseMessage("No Q doesn't make sense for grids larger than 5x5.");
        return false;
      }
      return true;
    }
  }

  /**
   * Main program.
   * @param args see help
   * @throws IOException if an I/O error occurs.
   */
  public static void main(final String[] args) throws IOException {
    final CliFlags flags = new CliFlags("Playfair", "Transform 26-letter text according to Playfair");
    flags.registerRequired('k', KEY_FLAG, String.class, "key", "The key text");
    flags.registerOptional('d', DECODE_FLAG, "Decode the message (default is to encode)");
    flags.registerOptional(DUMMY_FLAG, Character.class, "letter", "Character to use as the dummy letter", 'X');
    flags.registerOptional(NO_Q_FLAG, "Use the variant with no Q rather than combining I and J");
    flags.registerOptional('6', SIX_FLAG, "Use 6x6 Playfair");
    flags.registerOptional(SEVEN_BY_FOUR_FLAG, "Use 7x4 Alam-Khalid-Salam Playfair");
    flags.registerOptional(EIGHT_FLAG, "Use 8x8 Playfair");
    flags.registerOptional(SCD_FLAG, "Use the Shrivastava-Chouhan-Dhawan key schedule (not recommended)");
    flags.setValidator(new PlayfairFlagsValidator());
    flags.setFlags(args);
    final String key = (String) flags.getValue(KEY_FLAG);

    final int width;
    final int height;
    final char dummy;
    final char padding;
    if (flags.isSet(SIX_FLAG)) {
      width = 6;
      height = 6;
      dummy = (Character) flags.getValue(DUMMY_FLAG);
      padding = dummy;
    } else if (flags.isSet(SEVEN_BY_FOUR_FLAG)) {
      width = 4;
      height = 7;
      dummy = '*';
      padding = '#';
    } else if (flags.isSet(EIGHT_FLAG)) {
      width = 8;
      height = 8;
      dummy = '*';
      padding = '#';
    } else {
      width = 5;
      height = 5;
      dummy = (Character) flags.getValue(DUMMY_FLAG);
      padding = dummy;
    }

    final Playfair playfair = new Playfair(key, height, width, dummy, padding, flags.isSet(NO_Q_FLAG), false);
    try (final BufferedInputStream is = new BufferedInputStream(System.in)) {
      System.out.println(playfair.transform(is, !flags.isSet(DECODE_FLAG)));
    }
  }
}
