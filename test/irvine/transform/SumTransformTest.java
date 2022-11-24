package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class SumTransformTest extends TestCase {

  public void testFilter0() {
    final Transform t = new SumTransform(0);
    assertEquals("0", t.apply(""));
    assertEquals("21", t.apply("CAT"));
    assertEquals("21", t.apply("Cat"));
    assertEquals("25", t.apply("z"));
  }

  public void testFilter1() {
    final Transform t = new SumTransform(1);
    assertEquals("0", t.apply(""));
    assertEquals("24", t.apply("CAT"));
    assertEquals("24", t.apply("Cat"));
    assertEquals("26", t.apply("z"));
  }
}
