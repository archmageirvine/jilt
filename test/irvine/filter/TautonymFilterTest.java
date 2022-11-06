package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class TautonymFilterTest extends TestCase {

  public void testIllegalRepeat() {
    try {
      new TautonymFilter(0);
      fail("Bad repeat");
    } catch (final IllegalArgumentException e) {
      assertEquals("Repeats must be at least 1", e.getMessage());
    }
    try {
      new TautonymFilter(-1);
      fail("Bad repeat");
    } catch (final IllegalArgumentException e) {
      assertEquals("Repeats must be at least 1", e.getMessage());
    }
  }

  public void testNull() {
    for (int i = 1; i < 10; ++i) {
      assertTrue(new TautonymFilter(i).is(null));
    }
  }

  public void testEmpty() {
    // matter of choice, we have accepted empty string
    for (int i = 1; i < 10; ++i) {
      assertTrue(new TautonymFilter(i).is(""));
    }
  }

  public void test1() {
    final TautonymFilter tautonym = new TautonymFilter(1);
    assertTrue(tautonym.is("a"));
    assertTrue(tautonym.is("aa"));
    assertTrue(tautonym.is("ab"));
    assertTrue(tautonym.is("abc"));
    assertTrue(tautonym.is("aba"));
    assertTrue(tautonym.is("abcdefg"));
  }

  public void test2() {
    final TautonymFilter tautonym = new TautonymFilter(2);
    assertTrue(tautonym.is("aa"));
    assertTrue(tautonym.is("aaaa"));
    assertTrue(tautonym.is("beriberi"));
    assertTrue(tautonym.is("papa"));
    assertTrue(tautonym.is("murmur"));
    assertFalse(tautonym.is("a"));
    assertFalse(tautonym.is("aaa"));
    assertFalse(tautonym.is("aba"));
    assertFalse(tautonym.is("the"));
    assertFalse(tautonym.is("dog"));
  }

  public void test3() {
    final TautonymFilter tautonym = new TautonymFilter(3);
    assertTrue(tautonym.is("aaa"));
    assertTrue(tautonym.is("thethethe"));
    assertTrue(tautonym.is("beriberiberi"));
    assertTrue(tautonym.is("papapa"));
    assertTrue(tautonym.is("murmurmur"));
    assertFalse(tautonym.is("a"));
    assertFalse(tautonym.is("aa"));
    assertFalse(tautonym.is("murmur"));
    assertFalse(tautonym.is("aba"));
    assertFalse(tautonym.is("the"));
    assertFalse(tautonym.is("dog"));
  }
}
