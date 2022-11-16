package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ReverseTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new ReverseTransform();
    assertEquals("", t.apply(""));
    assertEquals("TCA", t.apply("ACT"));
    assertEquals("TAC", t.apply("CAT"));
    assertEquals("ACT", t.apply("TCA"));
  }
}
