package irvine.wordsmith;

/**
 * Check for decrementing letters through words.
 * @author Sean A. Irvine
 */
public class ReverseLetterSequenceInspector extends LetterSequenceInspector {

  @Override
  protected char next(final char c) {
    return c == 'A' ? 'Z' : (char) (c - 1);
  }
}
