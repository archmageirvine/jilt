package irvine.transform;

import java.util.Locale;

/**
 * Lowercase the input.
 * @author Sean A. Irvine
 */
public class LowercaseTransform implements Transform {

  @Override
  public String apply(final String word) {
    return word.toLowerCase(Locale.getDefault());
  }
}
