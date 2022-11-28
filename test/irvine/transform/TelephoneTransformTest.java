package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TelephoneTransformTest extends TestCase {

  public void test() {
    final Transform t = new TelephoneTransform();
    assertEquals("", t.apply(null));
    assertEquals("", t.apply(""));
    assertEquals(" ", t.apply(" "));
    assertEquals(" ", t.apply("\t"));
    assertEquals(" ", t.apply("\n"));
    assertEquals("   ", t.apply("!@#"));
    assertEquals("22233344455566677778889999", t.apply("abcdefghijklmnopqrstuvwxyz"));
    assertEquals("22233344455566677778889999", t.apply("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }
}
