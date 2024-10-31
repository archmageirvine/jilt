package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class AddTransformTest extends TestCase {

  public void testFilter() {
    final Transform t = new AddTransform("ABC", 1);
    assertEquals("", t.apply(""));
    assertEquals("BCD", t.apply("AAA"));
    assertEquals("bcd", t.apply("aaa"));
    assertEquals("A!c!S!", t.apply("Z!a!P!"));
  }
}
