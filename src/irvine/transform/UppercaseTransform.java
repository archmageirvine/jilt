package irvine.transform;

import java.util.Locale;

/**
 * Uppercase the input.
 * @author Sean A. Irvine
 */
public class UppercaseTransform implements Transform {

  @Override
  public String apply(final String word) {
    return word.toUpperCase(Locale.getDefault());
  }

  @Override
  public String getName() {
    return "UPPER";
  }
}
