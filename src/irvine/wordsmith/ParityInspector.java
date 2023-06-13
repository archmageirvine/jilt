package irvine.wordsmith;

/**
 * Check for parity patterns.
 * @author Sean A. Irvine
 */
public class ParityInspector implements Inspector {

  private boolean isConstant(final String v, final String[] c) {
    for (final String w : c) {
      if (!w.equals(v)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 5) {
      return null;
    }
    final String[] c = new String[words.length];
    for (int j = 0; j < words.length; ++j) {
      final String w = words[j];
      int p = w.charAt(0) & 1;
      boolean constant = true;
      boolean alternate = true;
      for (int k = 1; k < w.length(); ++k) {
        final int t = w.charAt(k) & 1;
        if (constant) {
          constant = t == p;
        }
        if (alternate) {
          alternate = (p ^ (k & 1)) == t;
        }
      }
      if (constant) {
        c[j] = "C";
      } else if (alternate) {
        c[j] = "A";
      } else {
        c[j] = "-";
      }
    }
    // More checking could be done here -- parity syndromes
    if (isConstant("C", c)) {
      return "All words have constant parity";
    } else if (isConstant("A", c)) {
      return "All words have alternating parity";
    }
    return null;
  }
}
