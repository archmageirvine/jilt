package irvine.transform;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ScrabbleTransformTest extends TestCase {

  public void test1() {
    final ScrabbleTransform t = new ScrabbleTransform();
    assertEquals("0", t.apply(null));
    assertEquals("0", t.apply(""));
    assertEquals("0", t.apply(" "));
    assertEquals("0", t.apply("\t"));
    assertEquals("0", t.apply("\n"));
    assertEquals("0", t.apply("!@#"));
    assertEquals("0", t.apply("!!!"));
    assertEquals("0", t.apply(".,;<>{}"));
    assertEquals("0", t.apply("0123456789"));
  }

  public void test2() {
    final ScrabbleTransform t = new ScrabbleTransform();
    assertEquals("10", t.apply("z"));
    assertEquals("10", t.apply("Z"));
    assertEquals("1", t.apply("A"));
    assertEquals("1", t.apply("a"));
    assertEquals("10", t.apply("Q"));
    assertEquals("10", t.apply("q"));
    assertEquals("28", t.apply("QzX"));
    assertEquals("3", t.apply("tee"));
    assertEquals("6", t.apply("the"));
    assertEquals("2", t.apply("a-a"));
    assertEquals("2", t.apply("!-e-!e*"));
    assertEquals("87", t.apply("abcdefghijklmnopqrstuvwxyz"));
  }
}
