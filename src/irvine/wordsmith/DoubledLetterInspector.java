package irvine.wordsmith;

/**
 * Check for doubled letters.
 * @author Sean A. Irvine
 */
public class DoubledLetterInspector implements Inspector {

  private String doubleLetters(final String word) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 1; k < word.length(); ++k) {
      if (word.charAt(k) == word.charAt(k - 1)) {
        sb.append(word.charAt(k));
      }
    }
    return sb.toString();
  }

  @Override
  public String inspect(final String... words) {
    final StringBuilder sb = new StringBuilder();
    for (final String w : words) {
      final String t = doubleLetters(w);
      if (t.isEmpty()) {
        return null;
      }
      sb.append(t);
    }
    return "Every word has a doubled letter: " + sb;
  }
}
