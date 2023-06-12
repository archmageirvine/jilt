package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class SubstringFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new SubstringFilter("erudite");
    assertTrue(filter.is(""));
    assertFalse(filter.is("a"));
    assertTrue(filter.is("eru"));
    assertFalse(filter.is("erv"));
    assertTrue(filter.is("rudi"));
    assertFalse(filter.is("zudi"));
    assertTrue(filter.is("erudite"));
    assertTrue(filter.is("e"));
    assertFalse(filter.is("re"));
  }
}
