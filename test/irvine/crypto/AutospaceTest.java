package irvine.crypto;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 *
 * @author Sean A. Irvine
 */
public class AutospaceTest extends TestCase {

  public void test() {
    assertEquals("the quick brown fox jumped over the lazy dog.", Autospace.autospace("thequickbrownfoxjumpedoverthelazydog."));
  }
}
