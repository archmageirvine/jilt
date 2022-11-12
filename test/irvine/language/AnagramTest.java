package irvine.language;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class AnagramTest extends TestCase {

  public void checkNonePatterns(final String pattern) throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\nthe\nrandom".getBytes())) {
      assertEquals(0, Anagram.findAnagrams(pattern, is).size());
    }
  }

  public void checkOnePatterns(final String pattern) throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\nthe\nrandom".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams(pattern, is);
      assertEquals(1, r.size());
      assertEquals(pattern.toLowerCase(Locale.getDefault()), r.get(0));
    }
  }

  public void checkAndPatterns(final String pattern) throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\nthe\nrandom".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams(pattern, is);
      assertEquals(1, r.size());
      assertEquals("and", r.get(0));
    }
  }

  public void testNulls() throws IOException {
    try {
      assertNull(Anagram.findAnagrams("the", (InputStream) null));
      fail("Expected NPE");
    } catch (final NullPointerException e) {
      // ok
    }
  }

  public void test1() throws IOException {
    checkNonePatterns("");
  }

  public void test2() throws IOException {
    checkNonePatterns("a");
  }

  public void test3() throws IOException {
    checkNonePatterns("A");
  }

  public void test4() throws IOException {
    checkNonePatterns(".");
  }

  public void test5() throws IOException {
    checkNonePatterns("-");
  }

  public void test6() throws IOException {
    checkNonePatterns("and\n");
  }

  public void test7() throws IOException {
    checkNonePatterns("anda");
  }

  public void test8() throws IOException {
    checkNonePatterns("dog");
  }

  public void test9() throws IOException {
    checkOnePatterns("random");
  }

  public void test10() throws IOException {
    checkOnePatterns("and");
  }

  public void test11() throws IOException {
    checkOnePatterns("AND");
  }

  public void test12() throws IOException {
    checkOnePatterns("aND");
  }

  public void test13() throws IOException {
    checkAndPatterns("DaN");
  }

  public void test14() throws IOException {
    checkAndPatterns(".ND");
  }

  public void test15() throws IOException {
    checkAndPatterns(".D.");
  }

  public void test16() throws IOException {
    checkAndPatterns("a..");
  }

  public void test17() throws IOException {
    checkAndPatterns("Dna");
  }

  public void test18() throws IOException {
    checkAndPatterns("..a");
  }

  public void test19() throws IOException {
    checkNonePatterns("ane");
  }

  public void test20() throws IOException {
    checkNonePatterns("ae.");
  }

  public void test21() throws IOException {
    checkNonePatterns("end");
  }

  public void test22() throws IOException {
    checkNonePatterns("eee");
  }

  public void test23() throws IOException {
    checkNonePatterns("ean");
  }

  public void test24() throws IOException {
    checkAndPatterns("..n");
  }

  public void test25() throws IOException {
    checkAndPatterns("..d");
  }

  public void test26() throws IOException {
    checkAndPatterns(".d.");
  }

  public void testDittedPatterns() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\nthe\nrandom".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams("...", is);
      assertEquals(2, r.size());
      assertEquals("and", r.get(0));
      assertEquals("the", r.get(1));
    }
  }

  public void testTwinPatterns() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\ndna\nrandom".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams("dna", is);
      assertEquals(2, r.size());
      assertEquals("and", r.get(0));
      assertEquals("dna", r.get(1));
    }
  }

  public void testDifficultDit() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("and\naaa\n".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams("..a", is);
      assertEquals(2, r.size());
      assertEquals("and", r.get(0));
      assertEquals("aaa", r.get(1));
    }
  }

  public void testDifficultDit2() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("zaa\nazz\n".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams(".z.", is);
      assertEquals(2, r.size());
      assertEquals("zaa", r.get(0));
      assertEquals("azz", r.get(1));
    }
  }

  public void testDifficultDit3() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("zbaae\nabzze\n".getBytes())) {
      final ArrayList<String> r = Anagram.findAnagrams("..az.", is);
      assertEquals(2, r.size());
      assertEquals("zbaae", r.get(0));
      assertEquals("abzze", r.get(1));
    }
  }

}
