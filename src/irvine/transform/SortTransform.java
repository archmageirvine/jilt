package irvine.transform;

import java.util.Arrays;

/**
 * Sort the letters of the word.
 * @author Sean A. Irvine
 */
public class SortTransform implements Transform {

  @Override
  public String getName() {
    return "SORT";
  }

  @Override
  public String apply(final String s) {
    final char[] c = s.toCharArray();
    Arrays.sort(c);
    return new String(c);
  }
}
