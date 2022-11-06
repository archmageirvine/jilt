package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ReverseAlphabeticalFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new ReverseAlphabeticalFilter();
    assertTrue(filter.is(null));
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("baz"));
    assertTrue(filter.is("zba"));
    assertTrue(filter.is("hhhd"));
  }
}
