package irvine.wordsmith;

import java.util.ArrayList;
import java.util.List;

/**
 * An inspector that consults an existing word list.
 * @author Sean A. Irvine
 */
public class ListSubstringInspector implements Inspector {

  private final WordList mList;

  ListSubstringInspector(final String file) {
    mList = new WordList(file);
  }

  private String search(final String word) {
    for (final String t : mList.keySet()) {
      if (t.contains(word) && !t.equals(word)) {
        return t;
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    final List<String> res = new ArrayList<>();
    for (final String w : words) {
      final String t = search(w);
      if (t == null) {
        return null;
      }
      res.add(t);
    }
    return "All words are substrings of " + mList.getDescription() + "\n" + res;
  }
}
