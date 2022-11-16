package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class SortTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new SortTransform();
    assertEquals("", t.apply(""));
    assertEquals("ACT", t.apply("ACT"));
    assertEquals("ACT", t.apply("CAT"));
    assertEquals("ACT", t.apply("TCA"));
  }
}
