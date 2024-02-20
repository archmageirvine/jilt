package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ExactCountFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new ExactCountFilter("aeiou", 1);
    assertFalse(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("spry"));
    assertFalse(filter.is("z%b"));
    assertTrue(filter.is("hhhu"));
  }
}
