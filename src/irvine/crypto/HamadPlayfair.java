package irvine.crypto;

/**
 * Implementation of Playfair extended algorithm described by Safwat Hamad in
 * "A Novel Implementation of an Extended <code>8x8</code> Playfair Cipher Using Interweaving
 * on DNA-encoded Data".  Note this cipher is weak.
 * @author Sean A. Irvine
 */
public class HamadPlayfair {

  // Encoding according to Figure 4 of paper.
  private static final char[] DNA = {'A', 'C', 'G', 'T'};

  // The method is defined for ASCII, so technically the byte array below
  // should only contain valid ASCII value, but it might exhibit reasonable
  // behaviour for other values as well.
  String toDna(final byte[] message) {
    // Binary byte b7b6...b0, is converted by nibbles to nt0nt1nt2nt3
    final StringBuilder sb = new StringBuilder();
    for (int v : message) {
      for (int k = 0; k < 4; ++k) {
        sb.append(DNA[(v >>> 6) & 3]);
        v <<= 2;
      }
    }
    return sb.toString();
  }

  private static int nucleotideToValue(final char nt) {
    switch (nt) {
      case 'A':
        return 0;
      case 'C':
        return 1;
      case 'G':
        return 2;
      case 'T':
        return 3;
      default:
        throw new IllegalArgumentException("Unexpected nucleotide: " + nt);
    }
  }

  byte[] toMessage(final String dna) {
    if ((dna.length() & 3) != 0) {
      throw new IllegalArgumentException("DNA should be a multiple of 4 in length");
    }
    final byte[] res = new byte[dna.length() >> 2];
    for (int k = 0, j = 0; k < res.length; k++) {
      res[k] = (byte) ((nucleotideToValue(dna.charAt(j++)) << 6)
        + (nucleotideToValue(dna.charAt(j++)) << 4)
        + (nucleotideToValue(dna.charAt(j++)) << 2)
        + nucleotideToValue(dna.charAt(j++)));
    }
    return res;
  }

//  private static final String KEY_FLAG = "key";
//  private static final String DECODE_FLAG = "decode";
//  private static final String DUMMY_FLAG = "dummy";
//  private static final String NO_Q_FLAG = "noq";
//  private static final String SIX_FLAG = "six";
//
//  private static final String FIVE_ALPHABET_NO_J = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//  private static final String SIX_ALPHABET = FIVE_ALPHABET_NO_J + "0123456789";
//
//  private static char[] buildKey(final String key, final int gridWidth, final boolean noQ) {
//    final HashSet<Character> seen = new HashSet<>();
//    final char[] grid = new char[gridWidth * gridWidth]; // To be interpreted as 5x5 or 6x6 later
//    // Use characters appearing in key, but care is needed with repeats and I=J (or similar)
//    int j = 0;
//    for (int k = 0; k < key.length(); ++k) {
//      char c = Character.toUpperCase(key.charAt(k));
//      if (gridWidth == 5) {
//        if (noQ) {
//          if (c == 'Q') {
//            continue; // Skip Q
//          }
//        } else {
//          if (c == 'J') {
//            c = 'I';
//          }
//        }
//      }
//      if (c >= 'A' && c <= 'Z' && seen.add(c)) {
//        grid[j++] = c;
//      } else if (gridWidth == 6 && c >= '0' && c <= '9' && seen.add(c)) {
//        grid[j++] = c;
//      }
//    }
//    // Write in remaining letters
//    if (gridWidth == 5) {
//      if (noQ) {
//        seen.add('Q'); // Make it as if we have used Q
//      } else {
//        seen.add('J'); // Make it as if we have used J
//      }
//    }
//    final String alphabet = gridWidth == 5 ? FIVE_ALPHABET_NO_J : SIX_ALPHABET;
//    for (int k = 0; k < alphabet.length(); ++k) {
//      final char c = alphabet.charAt(k);
//      if (seen.add(c)) {
//        grid[j++] = c;
//      }
//    }
//    assert (gridWidth == 5 && j == 25) || (gridWidth == 6 && j == 36);
//    return grid;
//  }
//
//  private final String mKey;
//  private final char mDummy;
//  private final boolean mNoQ;
//  private final int mGridWidth;
//
//  HamadPlayfair(final String key, final int gridWidth, final char dummy, final boolean noQ) {
//    mKey = new String(buildKey(key, gridWidth, noQ));
//    mGridWidth = gridWidth;
//    mDummy = dummy;
//    mNoQ = noQ;
//  }
//
//  private char getNextCharToEncode(final InputStream is, final boolean noq) throws IOException {
//    int c;
//    while ((c = is.read()) >= 0) {
//      final char d = Character.toUpperCase((char) c);
//      if (d >= 'A' && d <= 'Z' && (d != 'Q' || !noq || mGridWidth == 6)) {
//        return d;
//      }
//      if (mGridWidth == 6 && d >= '0' && d <= '9') {
//        return d;
//      }
//    }
//    return 0; // No more characters
//  }
//
//  void transform(final char a, final char b, final PrintStream out, final boolean encode) {
//    final int delta = encode ? 1 : mGridWidth - 1; // i.e. +1, -1 mod mGridWith
//    final int za = mKey.indexOf(a);
//    final int zb = mKey.indexOf(b);
//    assert za >= 0;
//    assert zb >= 0;
//    final int xa = za % mGridWidth;
//    final int ya = za / mGridWidth;
//    final int xb = zb % mGridWidth;
//    final int yb = zb / mGridWidth;
//    // (xa,ya) and (xb,yb) are now the corners of the rectangle in the key grid
//    final int nxa;
//    final int nya;
//    final int nxb;
//    final int nyb;
//    if (ya == yb) { // same row
//      nya = ya;
//      nyb = yb;
//      nxa = (xa + delta) % mGridWidth;
//      nxb = (xb + delta) % mGridWidth;
//    } else if (xa == xb) { // same col
//      nxa = xa;
//      nxb = xb;
//      nya = (ya + delta) % mGridWidth;
//      nyb = (yb + delta) % mGridWidth;
//    } else { // rectangle
//      nya = ya;
//      nxa = xb;
//      nyb = yb;
//      nxb = xa;
//    }
//    final int nza = mGridWidth * nya + nxa;
//    final int nzb = mGridWidth * nyb + nxb;
//    out.print(mKey.charAt(nza));
//    out.print(mKey.charAt(nzb));
//  }
//
//  void transform(final InputStream is, final PrintStream out, final boolean encode) throws IOException {
//    char prev = 0; // Remembers any previous character not yet encoded
//    while (true) {
//      // Attempt to get a pair of characters (a,b) to encode.  We need a != b.
//      final char a;
//      if (prev != 0) {
//        a = prev;
//        prev = 0;
//      } else {
//        a = getNextCharToEncode(is, mNoQ);
//        if (a == 0) {
//          break; // We have reached EOF
//        }
//      }
//      final char b = getNextCharToEncode(is, mNoQ);
//      if (a == b) {
//        // Playfair cannot handle this, we need to use a dummy and stash this b for later
//        if (a == mDummy) {
//          throw new IllegalArgumentException("Input contains consecutive dummy (" + mDummy + ") characters, choose a different dummy");
//        }
//        prev = b;
//        transform(a, mDummy, out, encode);
//      } else if (b == 0) {
//        // We are left with "a" at EOF, use a dummy for "b"
//        transform(a, mDummy, out, encode);
//        break; // EOF
//      } else {
//        transform(a, b, out, encode);
//      }
//    }
//  }
//
//  // todo auto dummy removal on decode?
//
//  /**
//   * Main program. Arguments ignored.
//   * @param args ignored
//   * @throws IOException if an I/O error occurs.
//   */
//  public static void main(final String[] args) throws IOException {
//    final CliFlags flags = new CliFlags("Playfair", "Transform 26-letter text according to Playfair");
//    flags.registerRequired('k', KEY_FLAG, String.class, "key", "The key text");
//    flags.registerOptional('d', DECODE_FLAG, "Decode the message (default is to encode)");
//    flags.registerOptional(DUMMY_FLAG, Character.class, "letter", "Character to use as the dummy letter", 'X');
//    flags.registerOptional(NO_Q_FLAG, "Use the variant with no Q rather than combining I and J");
//    flags.registerOptional('6', SIX_FLAG, "Use 6x6 Playfair rather than 5x5 Playfair");
//    flags.setFlags(args);
//    final String key = (String) flags.getValue(KEY_FLAG);
//    final HamadPlayfair playfair = new HamadPlayfair(key, flags.isSet(SIX_FLAG) ? 6 : 5, (Character) flags.getValue(DUMMY_FLAG), flags.isSet(NO_Q_FLAG));
//    try (final BufferedInputStream is = new BufferedInputStream(System.in)) {
//      playfair.transform(is, System.out, !flags.isSet(DECODE_FLAG));
//      System.out.println(); // Writes CR at end of message
//    }
//  }
}
