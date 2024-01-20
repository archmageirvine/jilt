package irvine.transform;

/**
 * Report the count of the numbers of matching letters.
 * @author Sean A. Irvine
 */
public class MatchTransform implements Transform {

  private final String mMatch;

  /**
   * Construct a matcher for the specified word.
   * @param match matching string
   */
  public MatchTransform(final String match) {
    mMatch = match;
  }

  @Override
  public String getName() {
    return "MATCH(" + mMatch + ")";
  }

  @Override
  public String apply(final String s) {
    int cnt = 0;
    for (int k = 0; k < s.length(); ++k) {
      if (mMatch.indexOf(s.charAt(k)) >= 0) {
        ++cnt;
      }
    }
    return String.valueOf(cnt);
  }
}
