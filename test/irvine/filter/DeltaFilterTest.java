package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class DeltaFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new DeltaFilter("ah");
    assertFalse(filter.is(""));
    assertFalse(filter.is("a"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("ah"));
    assertFalse(filter.is("AH"));
    assertFalse(filter.is("bz"));
    assertTrue(filter.is("aa"));
    assertTrue(filter.is("bh"));
    assertTrue(filter.is("%H"));
  }
}
