package irvine.wordsmith;

/**
 * Check for incrementing letters through words.
 * @author Sean A. Irvine
 */
public class LetterSequenceInspector implements Inspector {

  protected char next(final char c) {
    return c == 'Z' ? 'A' : (char) (c + 1);
  }

  private boolean is(final char letter, final String... words) {
    char s = letter;
    for (final String w : words) {
      if (w.indexOf(s) < 0) {
        return false;
      }
      s = next(s);
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 6) {
      return null;
    }
    for (char start = 'A'; start <= 'Z'; ++start) {
      if (is(start, words)) {
        return "First word contains " + start + ", second word contains " + next(start) + ", and so on";
      }
    }
    return null;
  }
}
