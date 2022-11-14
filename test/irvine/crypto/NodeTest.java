package irvine.crypto;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class NodeTest extends TestCase {

  public void test() {
    final Node node = new Node(new char[42], 0.5);
    assertEquals(0.5, node.getScore());
    assertEquals(42, node.getPermutation().length);
    assertEquals(0, node.compareTo(node));
  }
}
