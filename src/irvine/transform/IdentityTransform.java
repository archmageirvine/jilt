package irvine.transform;

/**
 * The do nothing transform.
 * @author Sean A. Irvine
 */
public class IdentityTransform implements Transform {

  @Override
  public String getName() {
    return "IDENTITY";
  }

  @Override
  public String apply(final String word) {
    return word;
  }
}
