package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class LetterTransformTest extends TestCase {

  public void test1() {
    final Transform t = new LetterTransform();
    assertEquals("", t.apply(""));
    assertEquals("", t.apply(" "));
    assertEquals("", t.apply("\t"));
    assertEquals("", t.apply("\n"));
    assertEquals("!@#", t.apply("!@#"));
    assertEquals("0123456789", t.apply("0123456789"));
    assertEquals("ABCZ", t.apply("1 2\t3  26"));
  }
}
