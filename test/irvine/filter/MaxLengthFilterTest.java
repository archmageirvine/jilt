package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class MaxLengthFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new MinLengthFilter(3);
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("aa"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("ahhh"));
  }
}
