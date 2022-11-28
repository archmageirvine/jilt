package irvine.filter;

import irvine.transform.TelephoneTransform;

/**
 * Test if the word has the specified number when dialed on a telephone.
 * @author Sean A. Irvine
 */
public class TelephoneFilter extends TelephoneTransform implements Filter {

  private final String mDial;

  /**
   * Construct a new filter for words using a particular dial.
   * @param dial allowed characters
   */
  public TelephoneFilter(final String dial) {
    mDial = dial;
  }

  @Override
  public boolean is(final String word) {
    return apply(word).equals(mDial);
  }
}
