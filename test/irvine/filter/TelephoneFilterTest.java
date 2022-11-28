package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TelephoneFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new TelephoneFilter("228");
    assertFalse(filter.is(""));
    assertFalse(filter.is("a"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("bAz"));
    assertFalse(filter.is("z%b"));
    assertTrue(filter.is("cat"));
  }
}
