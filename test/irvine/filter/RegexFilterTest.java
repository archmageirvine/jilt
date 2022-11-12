package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class RegexFilterTest extends TestCase {

  public void testFilter1() {
    final Filter filter = new RegexFilter("a");
    assertFalse(filter.is(""));
    assertTrue(filter.is("a"));
    assertFalse(filter.is("abz"));
  }

  public void testFilter2() {
    final Filter filter = new RegexFilter("a.*b");
    assertFalse(filter.is(""));
    assertTrue(filter.is("ab"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("zab"));
    assertTrue(filter.is("azb"));
    assertTrue(filter.is("azzb"));
  }
}
