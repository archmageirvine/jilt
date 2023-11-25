package irvine.transform;

import java.util.Locale;

/**
 * Perform simple substitution using the given key.
 * @author Sean A. Irvine
 */
public class SubstituteTransform implements Transform {

  private final String mKey;

  private static String buildKey(final String inKey, final boolean invert) {
    // Expand/contract the key to a permutation on A..Z
    final StringBuilder outKey = new StringBuilder();
    int seen = 0;
    for (int k = 0; k < inKey.length(); ++k) {
      final char c = inKey.charAt(k);
      if (c >= 'A' && c <= 'Z' && (seen & (1 << (c - 'A'))) == 0) {
        seen |= 1L << (c - 'A');
        outKey.append(c);
      }
    }
    for (int k = 0; k < 26; ++k) {
      if ((seen & (1 << k)) == 0) {
        outKey.append((char) ('A' + k));
      }
    }
    if (invert) {
      final char[] inverse = new char[outKey.length()];
      for (int k = 0; k < 26; ++k) {
        inverse[outKey.charAt(k) - 'A'] = (char) ('A' + k);
      }
      return new String(inverse);
    }
    return outKey.toString();
  }

  /**
   * Substitute with a specified key.
   * @param key the key string
   * @param invert invert the key (i.e., perform decryption)
   */
  public SubstituteTransform(final String key, final boolean invert) {
    mKey = buildKey(key.toUpperCase(Locale.getDefault()), invert);
  }

  @Override
  public String getName() {
    return "SUBS(" + mKey + ")";
  }

  @Override
  public String apply(final String s) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      if (c >= 'A' && c <= 'Z') {
        sb.append(mKey.charAt(c - 'A'));
      } else if (c >= 'a' && c <= 'z') {
        sb.append(mKey.charAt(c - 'a'));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
