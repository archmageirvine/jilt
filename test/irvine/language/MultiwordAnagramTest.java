package irvine.language;

import java.io.IOException;

import irvine.StandardIoTestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class MultiwordAnagramTest extends StandardIoTestCase {

  public void test() throws IOException {
    final MultiwordAnagram ma = new MultiwordAnagram(System.out, 2);
    ma.addWord("marilyn");
    ma.addWord("MUNROE");
    ma.addWord("test");
    ma.search("mmlunraoeriyn", 2);
    assertEquals("marilyn munroe", getOut().trim());
  }
}
