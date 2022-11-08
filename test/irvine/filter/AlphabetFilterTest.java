package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class AlphabetFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new AlphabetFilter("ah");
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("bAz"));
    assertFalse(filter.is("z%b"));
    assertTrue(filter.is("hhha"));
  }
}
