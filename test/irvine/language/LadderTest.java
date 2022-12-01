package irvine.language;

import java.util.HashSet;
import java.util.Set;

import irvine.StandardIoTestCase;
import irvine.TestUtils;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class LadderTest extends StandardIoTestCase {

  public void testUp() {
    final Set<String> words = new HashSet<>();
    words.add("a");
    words.add("ab");
    words.add("abcd");
    words.add("abcde");
    words.add("abd");
    words.add("bad");
    final Ladder ladder = new Ladder();
    ladder.setWords(words);
    ladder.initUp();
    ladder.solveUp(System.out, "a", "a");
    assertEquals("a -> ab -> abd -> abcd -> abcde", getOut().trim());
  }

  public void testUpAnagram() {
    final Set<String> words = new HashSet<>();
    words.add("a");
    words.add("ab");
    words.add("abcd");
    words.add("abcde");
    words.add("abd");
    words.add("bad");
    final Ladder ladder = new Ladder();
    ladder.setWords(words);
    ladder.setAnagrams(true);
    ladder.initUp();
    ladder.solveUp(System.out, "a", "a");
    final String out = getOut();
    TestUtils.containsAll(out,
      "a -> ab -> abd -> abcd -> abcde",
      "a -> ab -> bad -> abcd -> abcde"
    );
  }
}
