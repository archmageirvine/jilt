package irvine.wordsmith;

import irvine.jilt.Dictionary;

/**
 * Check if they are in the dictionary.
 * @author Sean A. Irvine
 */
public class DictionaryInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    for (final String w : words) {
      if (!Dictionary.getDefaultDictionary().contains(w)) {
        return null;
      }
    }
    return "Every word is in the dictionary";
  }
}
