package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class DistinctFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new DistinctFilter();
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("bAb"));
    assertFalse(filter.is("%%"));
    assertTrue(filter.is("hH"));
  }
}
