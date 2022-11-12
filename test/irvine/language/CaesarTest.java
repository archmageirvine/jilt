package irvine.language;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class CaesarTest extends TestCase {

  public void testExplicit() {
    assertEquals("Dog", Caesar.caesarShift("Dog", 0));
    assertEquals("Eph", Caesar.caesarShift("Dog", 1));
    assertEquals("Cnf", Caesar.caesarShift("Dog", 25));
    assertEquals("Zazxqffqde % mdq xqrf mx0zq", Caesar.caesarShift("Nonletters % are left al0ne", 12));
  }

  public void testIdentity() {
    for (int k = 1; k < 14; ++k) {
      assertEquals("ABCXYZabcxyz", Caesar.caesarShift(Caesar.caesarShift("ABCXYZabcxyz", k), 26 - k));
    }
  }

  public void testIllegal() {
    try {
      Caesar.caesarShift("hello", -1);
      fail();
    } catch (final IllegalArgumentException e) {
      // ok
    }
    try {
      Caesar.caesarShift("hello", 26);
      fail();
    } catch (final IllegalArgumentException e) {
      // ok
    }
  }
}
