package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Set;

import irvine.jilt.Dictionary;
import irvine.language.Caesar;

/**
 * Check if every word has a Caesar shift.
 * @author Sean A. Irvine
 */
public class CaesarInspector implements Inspector {

  private String search(final Set<String> dict, final String word) {
    for (int shift = 1; shift < 26; ++shift) {
      final String caesar = Caesar.caesarShift(word, shift);
      if (dict.contains(caesar)) {
        return caesar;
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final ArrayList<String> res = new ArrayList<>();
    for (final String w : words) {
      final String r = search(dict, w);
      if (r == null) {
        return null;
      }
      res.add(r);
    }
    return "Every word has a Caesar shift\n" + res;
  }
}
