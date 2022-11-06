package irvine.transform;

import irvine.filter.Filter;
import irvine.filter.PalindromeFilter;
import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class UppercaseTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new UppercaseTransform();
    assertEquals("", t.apply(""));
    assertEquals("ABC", t.apply("aBc"));
  }
}
