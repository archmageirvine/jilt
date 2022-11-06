package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TitlecaseTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new TitlecaseTransform();
    assertEquals("", t.apply(""));
    assertEquals("Abc", t.apply("ABC"));
    assertEquals("Death  On\tThe\nNile", t.apply("death  on\tthe\nNiLe"));
  }
}
