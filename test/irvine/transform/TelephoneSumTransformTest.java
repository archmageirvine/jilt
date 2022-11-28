package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TelephoneSumTransformTest extends TestCase {

  public void test() {
    final Transform t = new TelephoneSumTransform();
    assertEquals("0", t.apply(null));
    assertEquals("0", t.apply(""));
    assertEquals("0", t.apply(" "));
    assertEquals("0", t.apply("\t"));
    assertEquals("0", t.apply("\n"));
    assertEquals("0", t.apply("!@#"));
    assertEquals("148", t.apply("abcdefghijklmnopqrstuvwxyz"));
    assertEquals("148", t.apply("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }
}
