package irvine.filter;

import java.util.Arrays;

import irvine.util.StringUtils;

/**
 * Find words matching particular counts of letters including the so-called
 * pyramid words.  Given a pattern like 221 the sum of the numbers is the
 * total length of the word (5 in this case) and the word must have two
 * different letters occurring twice each, and one letter unique.
 * @author Sean A. Irvine
 */
public class PyramidFilter implements Filter {

  private final int[] mSyndrome;
  private final int mLength;

  /**
   * Construct a new filter for words matching a letter pattern.
   * @param pattern pattern to be searched for
   */
  public PyramidFilter(final String pattern) {
    mSyndrome = new int[pattern.length()];
    int len = 0;
    for (int k = 0; k < mSyndrome.length; ++k) {
      mSyndrome[k] = pattern.charAt(k) - '0';
      if (mSyndrome[k] < 1 || mSyndrome[k] > 9) {
        throw new IllegalArgumentException();
      }
      len += mSyndrome[k];
    }
    Arrays.sort(mSyndrome);
    mLength = len;
  }

  @Override
  public boolean is(final String word) {
    if (word.length() != mLength) {
      return false;
    }
    final int[] counts = StringUtils.syndrome(word);
    Arrays.sort(counts);
    for (int k = 1; k <= mSyndrome.length; ++k) {
      if (counts[counts.length - k] != mSyndrome[mSyndrome.length - k]) {
        return false;
      }
    }
    return true;
  }
}
