package irvine.wordsmith;

/**
 * Check if the list is strictly alphabetical.
 * @author Sean A. Irvine
 */
public class AlphabeticalWordInspector implements Inspector {

  private boolean is(final String word) {
    for (int k = 1; k < word.length(); ++k) {
      if (word.charAt(k) < word.charAt(k - 1)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 2) {
      return null;
    }
    for (final String w : words) {
      if (is(w)) {
        return null;
      }
    }
    return "Letters of every word are in alphabetical order";
  }
}
