package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class MinLengthFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new MinLengthFilter(3);
    assertFalse(filter.is(""));
    assertFalse(filter.is("a"));
    assertFalse(filter.is("aa"));
    assertTrue(filter.is("abz"));
    assertTrue(filter.is("ahhh"));
  }
}
