package irvine.transform;

/**
 * Convert numbers in the range 1 to 26 into letters.
 * @author Sean A. Irvine
 */
public class LetterTransform implements Transform {

  @Override
  public String getName() {
    return "LETTERS";
  }

  @Override
  public String apply(final String word) {
    final StringBuilder res = new StringBuilder();
    for (final String p : word.split("\\s+")) {
      try {
        final int v = Integer.parseInt(p);
        if (v < 1 || v > 26) {
          res.append(p);
        } else {
          res.append((char) ('@' + v));
        }
      } catch (final NumberFormatException e) {
        res.append(p);
      }
    }
    return res.toString();
  }
}
