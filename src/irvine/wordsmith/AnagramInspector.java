package irvine.wordsmith;

import java.util.List;
import java.util.Set;

import irvine.language.Anagram;

/**
 * Check if all words are anagrams of words in a list.
 * @author Sean A. Irvine
 */
public class AnagramInspector implements Inspector {

  private final WordList mList;

  AnagramInspector(final String file) {
    mList = new WordList(file);
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = mList.keySet();
    for (final String w : words) {
      final List<String> a = Anagram.findAnagrams(w, dict);
      a.remove(w); // don't allow anagram to self
      if (a.isEmpty()) {
        return null;
      }
    }
    return "All words have an anagram in " + mList.getDescription();
  }
}
