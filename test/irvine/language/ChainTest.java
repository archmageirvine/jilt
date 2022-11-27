package irvine.language;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class ChainTest extends TestCase {

  public void test() {
    final List<String> words = new ArrayList<>(5);
    words.add("cat");
    words.add("car");
    words.add("xxx");
    words.add("cbr");
    words.add("bbr");
    words.add("rac");
    final Chain sc = new Chain();
    sc.setWords(words);
    assertTrue(sc.solve("car", "donkey").isEmpty());
    assertTrue(sc.solve("car", "xxx").isEmpty());
    assertTrue(sc.solve("car", "rac").isEmpty());
    assertEquals(2, sc.solve("cat", "car").size());
    assertEquals(4, sc.solve("cat", "bbr").size());
    sc.setAnagrams(true);
    assertTrue(sc.solve("car", "donkey").isEmpty());
    assertTrue(sc.solve("car", "xxx").isEmpty());
    assertEquals(2, sc.solve("car", "rac").size());
    assertEquals(2, sc.solve("cat", "car").size());
    assertEquals(4, sc.solve("cat", "bbr").size());
  }

  public void testSlide() {
    final List<String> words = new ArrayList<>(5);
    words.add("west");
    words.add("star");
    words.add("area");
    words.add("east");
    words.add("cart");
    words.add("stab");
    final Chain sc = new Chain();
    sc.setWords(words);
    sc.setSlide(2);
    assertTrue(sc.solve("car", "xxx").isEmpty());
    assertEquals("[west, star, area, east]", sc.solve("west", "east").toString());
  }
}
