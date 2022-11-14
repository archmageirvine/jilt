package irvine.crypto;

import irvine.util.Shuffle;

/**
 * Generate a random permutation of the alphabet.
 * @author Sean A. Irvine
 */
public final class RandomKey {

  private RandomKey() { }

  static char[] getRandomKey() {
    final char[] key = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    Shuffle.shuffle(key);
    return key;
  }

  /**
   * Generate a random permutation of the alphabet.
   * @param args ignored
   */
  public static void main(final String[] args) {
    final char[] key = getRandomKey();
    System.out.println(new String(key));
  }

}
