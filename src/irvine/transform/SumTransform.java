package irvine.transform;

/**
 * Sum the letters of a word.
 * @author Sean A. Irvine
 */
public class SumTransform implements Transform {

  private final int mOffset;

  /**
   * Sum transform with specified offset.
   * @param offset offset (typically 0 or 1)
   */
  public SumTransform(final int offset) {
    mOffset = offset;
  }

  @Override
  public String getName() {
    return "SUM" + mOffset;
  }

  @Override
  public String apply(final String s) {
    long sum = 0;
    for (int k = 0; k < s.length(); ++k) {
      final char c = Character.toUpperCase(s.charAt(k));
      if (c >= 'A' && c <= 'Z') {
        sum += c - 'A' + mOffset;
      }
    }
    return String.valueOf(sum);
  }
}
