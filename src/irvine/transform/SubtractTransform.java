package irvine.transform;

import java.util.Locale;

/**
 * Cyclically subtract a string to a string skipping over non-letter characters.
 * @author Sean A. Irvine
 */
public class SubtractTransform implements Transform {

  private static final String A = "ZABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String B = "zabcdefghijklmnopqrstuvwxyz";
  private final String mKey;
  private final int mOffset;

  /**
   * Subtract with a specified key.
   * @param key the key string
   * @param offset usually 0 or 1
   */
  public SubtractTransform(final String key, final int offset) {
    mKey = key.toUpperCase(Locale.getDefault());
    mOffset = offset;
  }

  @Override
  public String getName() {
    return "SUB(" + mKey + "," + mOffset + ")";
  }

  @Override
  public String apply(final String s) {
    final StringBuilder sb = new StringBuilder();
    int keyPos = 0;
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      final int shift = 26 - (mKey.charAt(keyPos) - 'A' + mOffset);
      if (shift < 0 || shift > 26) {
        sb.append(c);
      } else if (c >= 'A' && c <= 'Z') {
        final int e = (c - '@' + shift) % 26;
        sb.append(A.charAt(e));
        if (++keyPos == mKey.length()) {
          keyPos = 0;
        }
      } else if (c >= 'a' && c <= 'z') {
        final int e = (c - '`' + shift) % 26;
        sb.append(B.charAt(e));
        if (++keyPos == mKey.length()) {
          keyPos = 0;
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
