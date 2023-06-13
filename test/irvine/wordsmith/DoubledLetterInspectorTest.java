package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class DoubledLetterInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new DoubledLetterInspector();
    assertNull(inspector.inspect("a"));
    assertNull(inspector.inspect("aa", "abcdefgh"));
    assertNull(inspector.inspect("ZXHELELELELEL", "EE"));
    assertEquals("Every word has a doubled letter: cat", inspector.inspect("accede", "kraal", "attack"));
  }
}
