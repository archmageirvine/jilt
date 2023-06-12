package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class AlphabeticalInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new AlphabeticalInspector();
    assertNull(inspector.inspect("a"));
    assertNull(inspector.inspect("z", "a"));
    assertNull(inspector.inspect("b", "b"));
    assertEquals("Words are in alphabetical order", inspector.inspect("apple", "banana", "plum"));
  }
}
