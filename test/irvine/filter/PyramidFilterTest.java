package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class PyramidFilterTest extends TestCase {

  public void testFilter() {
    assertTrue(new PyramidFilter("21").is("all"));
    assertFalse(new PyramidFilter("21").is("arm"));
    assertFalse(new PyramidFilter("21").is("al"));
    assertFalse(new PyramidFilter("21").is("alla"));
    assertFalse(new PyramidFilter("111").is("all"));
    assertTrue(new PyramidFilter("111").is("arm"));
    assertFalse(new PyramidFilter("111").is("al"));
    assertFalse(new PyramidFilter("111").is("alla"));
    assertFalse(new PyramidFilter("111").is("#"));
  }
}
