package irvine.crypto;

import irvine.entropy.Entropy;

/**
 * Crack a Beaufort cipher.
 * @author Sean A. Irvine
 */
public class Beaufort extends Vigenere {

  /**
   * Construct a solver.
   * @param model the model
   * @param reverse assume reverse Vigenere
   * @param includeKeyEntropy should the key entropy be included in scoring
   */
  public Beaufort(final Entropy model, final boolean reverse, final boolean includeKeyEntropy) {
    super(model, reverse, includeKeyEntropy);
  }

  @Override
  protected StringBuilder decrypt(final String key, final String cipher, final boolean reverse) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < cipher.length(); ++k) {
      final int shift = key.charAt(k % key.length()) - 'A';
      if (shift < 0) {
        sb.append('\0');
      } else {
        final int plain = (shift + 26 - (cipher.charAt(k) - 'A')) % 26;
        sb.append((char) (plain + 'A'));
      }
    }
    return sb;
  }
}
