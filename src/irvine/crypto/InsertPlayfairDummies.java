package irvine.crypto;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * The Playfair cipher cannot handle bigrams of the same letter. This utility makes
 * such a stream.
 * @author Sean A. Irvine
 */
public final class InsertPlayfairDummies {

  private InsertPlayfairDummies() { }

  /**
   * Add dummies.
   * @param args dummy letter (default is X)
   * @throws IOException if an I/O error occurs.
   */
  public static void main(final String[] args) throws IOException {
    final byte dummy = (byte) (args.length > 0 ? args[0].charAt(0) : 'X');
    int prev = -1;
    boolean even = false;
    try (final BufferedInputStream is = new BufferedInputStream(System.in)) {
      int c;
      while ((c = is.read()) != -1) {
        if (c == prev && even) {
          System.out.write(dummy);
          System.out.write(c);
        } else {
          System.out.write(c);
          even = !even;
        }
        prev = c;
      }
    }
  }
}
