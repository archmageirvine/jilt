package irvine.wordsmith;

/**
 * Check for Roman numerals.
 * @author Sean A. Irvine
 */
public class DoReMiInspector implements Inspector {

  private static final String[] SYMBOLS = {"DO", "RE", "MI", "FA", "SO", "LA", "TI"};

  @Override
  public String inspect(final String... words) {
    if (words.length < 5) {
      return null;
    }
    for (int k = 0; k < words.length; ++k) {
      final String w = words[k];
      final String s = SYMBOLS[k % SYMBOLS.length];
      if (w.indexOf(s.charAt(0)) < 0 || w.indexOf(s.charAt(1)) < 0) {
        return null;
      }
    }
    return "Words contain musical scale do, re, mi, so, ...";
  }
}
