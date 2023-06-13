package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class LengthInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new LengthInspector();
    assertNull(inspector.inspect("a"));
    assertNull(inspector.inspect("a", "ab"));
    assertNull(inspector.inspect("a", "ab", "abcd"));
    assertEquals("All words have the same length: 3", inspector.inspect("cat", "dog", "eel"));
    assertEquals("Words increase in length by 2 letters at each step", inspector.inspect("cat", "kills", "chicken"));
    assertEquals("Words decrease in length by 2 letters at each step", inspector.inspect("chicken", "kills", "cat"));
  }
}
