package irvine.transform;

/**
 * Report the sum of the telephone encoding of each word. Any symbols not having
 * a corresponding code are considered to be 0.
 * @author Sean A. Irvine
 */
public class TelephoneSumTransform implements Transform {

  /** Number corresponding to A through Z. */
  private static final int[] DIAL = {2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9};

  @Override
  public String getName() {
    return "TELEPHONE-SUM";
  }

  @Override
  public String apply(final String word) {
    long sum = 0;
    if (word != null) {
      for (int i = 0; i < word.length(); ++i) {
        final char d;
        if ((d = Character.toLowerCase(word.charAt(i))) >= 'a' && d <= 'z') {
          sum += DIAL[d - 'a'];
        }
      }
    }
    return String.valueOf(sum);
  }
}
