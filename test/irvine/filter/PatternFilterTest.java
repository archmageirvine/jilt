package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class PatternFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new PatternFilter("11232");
    assertFalse(filter.is(""));
    assertFalse(filter.is("112321"));
    assertTrue(filter.is("11232"));
    assertTrue(filter.is("llama"));
    assertFalse(filter.is("11111"));
    assertFalse(filter.is("12345"));
    assertFalse(filter.is("12343"));
  }
}
