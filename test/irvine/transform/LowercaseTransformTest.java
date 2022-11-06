package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class LowercaseTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new LowercaseTransform();
    assertEquals("", t.apply(""));
    assertEquals("abc", t.apply("ABc"));
    assertEquals("12-dozen", t.apply("12-DozEn"));
  }
}
