package irvine.transform;

/**
 * Titlecase the input.
 * @author Sean A. Irvine
 */
public class TitlecaseTransform implements Transform {

  @Override
  public String apply(final String word) {
    final StringBuilder sb = new StringBuilder();
    boolean wasSpace = true;
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      if (Character.isWhitespace(c)) {
        wasSpace = true;
        sb.append(c);
      } else if (wasSpace) {
        sb.append(Character.toTitleCase(c));
        wasSpace = false;
      } else {
        sb.append(Character.toLowerCase(c));
      }
    }
    return sb.toString();
  }

  @Override
  public String getName() {
    return "TITLE";
  }
}
