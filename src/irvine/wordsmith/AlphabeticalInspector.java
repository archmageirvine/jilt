package irvine.wordsmith;

/**
 * Check if the list is strictly alphabetical.
 * @author Sean A. Irvine
 */
public class AlphabeticalInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 2) {
      return null;
    }
    String prev = null;
    for (final String w : words) {
      if (prev != null && w.compareTo(prev) <= 0) {
        return null;
      }
      prev = w;
    }
    return "Words are in alphabetical order";
  }
}
