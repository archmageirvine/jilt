package irvine.wordsmith;

import irvine.jilt.Dictionary;

/**
 * Check if they are in the dictionary.
 * @author Sean A. Irvine
 */
public class ReverseDictionaryInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    for (final String w : words) {
      if (!Dictionary.getDefaultDictionary().contains(new StringBuilder(w).reverse().toString())) {
        return null;
      }
    }
    return "All are words when reversed";
  }
}
