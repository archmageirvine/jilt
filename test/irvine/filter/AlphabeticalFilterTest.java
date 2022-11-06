package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class AlphabeticalFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new AlphabeticalFilter();
    assertTrue(filter.is(null));
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("baz"));
    assertFalse(filter.is("zab"));
    assertTrue(filter.is("ahhh"));
  }
}
