package irvine.transform;

/**
 * Convert letters into numerical equivalent.
 * @author Sean A. Irvine
 */
public class NumbersTransform implements Transform {

  @Override
  public String getName() {
    return "NUMBERS";
  }

  @Override
  public String apply(final String word) {
    final StringBuilder res = new StringBuilder();
    boolean wasNumber = false;
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      if (c >= 'A' && c <= 'Z') {
        if (res.length() > 0) {
          res.append(' ');
        }
        res.append(c - '@');
        wasNumber = true;
      } else if (c >= 'a' && c <= 'z') {
        if (res.length() > 0) {
          res.append(' ');
        }
        res.append(c - '`');
        wasNumber = true;
      } else {
        if (wasNumber) {
          if (c != ' ') {
            res.append(' ');
          }
          wasNumber = false;
        }
        res.append(c);
      }
    }
    return res.toString();
  }
}
