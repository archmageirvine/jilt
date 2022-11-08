package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ContainsFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new ContainsFilter("a");
    assertFalse(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("bAz"));
    assertFalse(filter.is("z%b"));
    assertTrue(filter.is("hhha"));
  }
}
