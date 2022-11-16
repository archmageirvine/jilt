package irvine.transform;

/**
 * Reverse the letters of the word.
 * @author Sean A. Irvine
 */
public class ReverseTransform implements Transform {

  @Override
  public String getName() {
    return "REVERSE";
  }

  @Override
  public String apply(final String s) {
    return new StringBuilder(s).reverse().toString();
  }
}
