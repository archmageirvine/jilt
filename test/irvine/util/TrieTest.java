package irvine.util;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TrieTest extends TestCase {

  public void testTrie() {
    final Trie root = new Trie(null, false);
    assertFalse(root.contains(""));
    assertFalse(root.contains("cat"));
    root.add("cat");
    assertFalse(root.contains(""));
    assertTrue(root.contains("cat"));
    assertFalse(root.contains("ca"));
    assertFalse(root.contains("cats"));
    root.add("car");
    assertFalse(root.contains(""));
    assertTrue(root.contains("cat"));
    assertTrue(root.contains("car"));
    assertFalse(root.contains("ca"));
    assertFalse(root.contains("cats"));
    root.add("cats");
    assertFalse(root.contains(""));
    assertTrue(root.contains("cat"));
    assertTrue(root.contains("car"));
    assertFalse(root.contains("ca"));
    assertTrue(root.contains("cats"));
  }
}
