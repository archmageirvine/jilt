package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class PalindromeFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new PalindromeFilter();
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("aa"));
    assertTrue(filter.is("aba"));
    assertTrue(filter.is("abba"));
    assertTrue(filter.is("abcdzzzdcba"));
    assertFalse(filter.is("ah"));
    assertFalse(filter.is("ahc"));
    assertFalse(filter.is("aah"));
  }
}
