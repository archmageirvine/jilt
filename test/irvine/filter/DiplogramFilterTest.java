package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class DiplogramFilterTest extends TestCase {

  public void testIllegalRepeat() {
    try {
      new DiplogramFilter(0);
      fail("Bad repeat");
    } catch (final IllegalArgumentException e) {
      assertEquals("Repeats must be at least 1", e.getMessage());
    }
    try {
      new DiplogramFilter(-1);
      fail("Bad repeat");
    } catch (final IllegalArgumentException e) {
      assertEquals("Repeats must be at least 1", e.getMessage());
    }
  }

  public void testNull() {
    for (int i = 1; i < 10; ++i) {
      assertTrue(new DiplogramFilter(i).is(null));
    }
  }

  public void testEmpty() {
    // matter of choice, we have accepted empty string
    for (int i = 1; i < 10; ++i) {
      assertTrue(new DiplogramFilter(i).is(""));
    }
  }

  public void test1() {
    final DiplogramFilter f = new DiplogramFilter(1);
    assertTrue(f.is("a"));
    assertFalse(f.is("aa"));
    assertTrue(f.is("abcdef"));
  }

  public void test2() {
    final DiplogramFilter f = new DiplogramFilter(2);
    assertFalse(f.is("a"));
    assertTrue(f.is("aa"));
    assertFalse(f.is("abcdef"));
    assertTrue(f.is("abccbaddZZ"));
  }

  public void test3() {
    final DiplogramFilter f = new DiplogramFilter(3);
    assertFalse(f.is("a"));
    assertFalse(f.is("aa"));
    assertTrue(f.is("aaa"));
    assertFalse(f.is("abcdef"));
    assertTrue(f.is("abccbaddZZZdabc"));
  }
}
