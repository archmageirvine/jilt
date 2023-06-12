package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ConstantInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new ConstantInspector();
    assertEquals("Every word is: a", inspector.inspect("a"));
    assertNull(inspector.inspect("a", "z"));
    assertEquals("Every word is: b", inspector.inspect("b", "b"));
    assertNull(inspector.inspect("plum", "banana", "apple"));
  }
}
