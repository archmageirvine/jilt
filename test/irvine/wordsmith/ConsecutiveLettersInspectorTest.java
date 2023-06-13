package irvine.wordsmith;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class ConsecutiveLettersInspectorTest extends TestCase {

  public void testFilter() {
    final Inspector inspector = new ConsecutiveLettersInspector();
    assertNull(inspector.inspect("A"));
    assertNull(inspector.inspect("A", "AB"));
    assertNull(inspector.inspect("ABC", "ABC", "ABC"));
    assertNull(inspector.inspect("ABC", "ABC", "ABC", "ABCD"));
    assertEquals("Every word contains exactly 3 consecutive letters", inspector.inspect("ABC", "ABC", "ABC", "ABCE"));
    assertEquals("Every word contains exactly 5 consecutive letters", inspector.inspect("BLACKENED", "DEFACING", "FREIGHT", "HIGHJACK", "POLICEMAN", "REQUEST", "OVERTURNS"));
  }
}
