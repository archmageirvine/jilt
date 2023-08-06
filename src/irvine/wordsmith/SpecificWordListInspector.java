package irvine.wordsmith;

import java.util.Arrays;

import irvine.util.StringUtils;

/**
 * Check for specific sets of words.
 * @author Sean A. Irvine
 */
public class SpecificWordListInspector implements Inspector {

  private final String[] mList;
  private final int[][] mSyndromes;

  SpecificWordListInspector(final String... list) {
    mList = list;
    mSyndromes = new int[list.length][];
    for (int k = 0; k < list.length; ++k) {
      mSyndromes[k] = StringUtils.syndrome(list[k]);
    }
  }

  private boolean lt(final int[] a, final int[] b) {
    for (int k = 0; k < a.length; ++k) {
      if (a[k] < b[k]) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 3 || words.length >= mSyndromes.length) {
      return null;
    }
    for (int k = 0; k < words.length; ++k) {
      final String w = words[k];
      final int[] syn = StringUtils.syndrome(w);
      if (lt(syn, mSyndromes[k])) {
        return null;
      }
    }
    return "Words contain: " + Arrays.toString(Arrays.copyOf(mList, words.length));
  }
}
