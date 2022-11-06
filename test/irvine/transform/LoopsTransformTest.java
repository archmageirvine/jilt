package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class LoopsTransformTest extends TestCase {

  public void testFilter() {
    assertEquals("23", new LoopsTransform().apply("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
  }
}
