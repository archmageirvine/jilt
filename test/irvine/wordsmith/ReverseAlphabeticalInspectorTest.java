package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ReverseAlphabeticalInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new ReverseAlphabeticalInspector();
    assertNull(inspector.inspect("a"));
    assertNull(inspector.inspect("a", "z"));
    assertNull(inspector.inspect("b", "b"));
    assertEquals("Words are in reverse alphabetical order", inspector.inspect("plum", "banana", "apple"));
  }
}
