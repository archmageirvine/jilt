package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Arrays;

import irvine.jilt.Dictionary;

/**
 * Check if they remain words when words from a specific list are inserted.
 * @author Sean A. Irvine
 */
public class InsertWordListInspector implements Inspector {

  private final String[] mList;

  InsertWordListInspector(final String... list) {
    mList = list;
  }

  private String is(final String word) {
    for (int k = 0; k <= word.length(); ++k) {
      final String left = word.substring(0, k);
      final String right = word.substring(k);
      for (final String insert : mList) {
        final String w = left + insert + right;
        if (Dictionary.getDefaultDictionary().contains(w)) {
          return w;
        }
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final ArrayList<String> res = new ArrayList<>();
    for (final String w : words) {
      final String r = is(w);
      if (r == null) {
        return null;
      }
      res.add(r);
    }
    return "Words from " + Arrays.toString(Arrays.copyOf(mList, Math.min(mList.length, 3))) + "... can be inserted to give:\n" + res;
  }
}
