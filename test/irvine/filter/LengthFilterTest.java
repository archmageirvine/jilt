package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class LengthFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new LengthFilter(3);
    assertFalse(filter.is(""));
    assertFalse(filter.is("a"));
    assertFalse(filter.is("aa"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("ahhh"));
  }
}
