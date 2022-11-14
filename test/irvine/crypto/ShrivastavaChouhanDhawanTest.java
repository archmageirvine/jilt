package irvine.crypto;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class ShrivastavaChouhanDhawanTest extends TestCase {

  public void test() {
    final char[] expected = "GMAIL.CBDEFHKPQRSTUVWXYZ0123789!JON_465@*-$#,/+?;%=&'\\)[]:<(>\"{}".toCharArray();
    assertTrue(Arrays.equals(expected, ShrivastavaChouhanDhawan.buildKey("JON_465@gmail.com", Playfair.EIGHT_BY_EIGHT_ALPHABET, 8)));
  }
}
