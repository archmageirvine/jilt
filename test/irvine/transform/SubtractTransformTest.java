package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class SubtractTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new SubtractTransform("ABC", 1);
    assertEquals("", t.apply(""));
    assertEquals("ZYX", t.apply("AAA"));
    assertEquals("zyx", t.apply("aaa"));
    assertEquals("Y!y!M!", t.apply("Z!a!P!"));
  }
}
