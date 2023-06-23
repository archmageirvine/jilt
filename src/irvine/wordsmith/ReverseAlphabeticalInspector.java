package irvine.wordsmith;

/**
 * Check if the list is reverse alphabetical.
 * @author Sean A. Irvine
 */
public class ReverseAlphabeticalInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 2) {
      return null;
    }
    String prev = null;
    for (final String w : words) {
      if (prev != null && w.compareTo(prev) >= 0) {
        return null;
      }
      prev = w;
    }
    return "Words are in reverse alphabetical order";
  }
}
