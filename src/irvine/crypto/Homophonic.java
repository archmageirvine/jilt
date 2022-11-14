package irvine.crypto;

import java.util.Random;

/**
 * Encode text as a homophonic cipher.
 * @author Sean A. Irvine
 */
public final class Homophonic {

  // This is pretty crappy at the moment.  Uses hard coded and boringly simple key.

  private Homophonic() { }

  private static final String DEFAULT_DISTRIBUTION = "AAAAAAAABBCCCDDDDEEEEEEEEEEEEFFGGHHHHHHIIIIIIJKLLLLMMNNNNNNOOOOOOOPPQRRRRRRSSSSSSTTTTTTTTTUUUVWWXYYZ";

  /**
   * Main.
   * @param args ignored
   */
  public static void main(final String[] args) {
    final Random random = new Random();
    final String plaintext = args[0];
    for (int k = 0; k < plaintext.length(); ++k) {
      final int c = Character.toUpperCase(plaintext.charAt(k));
      if (c < 'A' || c > 'Z') {
        continue;
      }
      final int lower = DEFAULT_DISTRIBUTION.indexOf(c);
      final int upper = DEFAULT_DISTRIBUTION.lastIndexOf(c);
      final int n = lower + random.nextInt(upper - lower + 1);
      System.out.println(n < 10 ? "0" + n : n);
    }
  }
}
