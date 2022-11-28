package irvine.filter;

import irvine.transform.TelephoneSumTransform;

/**
 * Test if the word has the specified digit sum when dialed on a telephone.
 * @author Sean A. Irvine
 */
public class TelephoneSumFilter extends TelephoneSumTransform implements Filter {

  private final String mDial;

  /**
   * Construct a new filter for words with a particular dialing sum.
   * @param dial allowed characters
   */
  public TelephoneSumFilter(final String dial) {
    mDial = dial;
  }

  @Override
  public boolean is(final String word) {
    return apply(word).equals(mDial);
  }
}
