package irvine.util;

import java.util.Random;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class IrvineHashFunctionTest extends AbstractHashFunctionTest {

  @Override
  public HashFunction getHashFunction() {
    return new IrvineHashFunction();
  }

  public void testCollision1() {
    final HashFunction h = getHashFunction();
    final String a = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    final String b = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
    final long hab = h.hash(a + b);
    final long hba = h.hash(b + a);
    assertEquals(hab, hba);
  }

  public void testCollision2() {
    final HashFunction h = getHashFunction();
    final Random r = new Random();
    final byte[] a = new byte[512];
    final byte[] b = new byte[512];
    r.nextBytes(a);
    System.arraycopy(a, 0, b, 256, 256);
    System.arraycopy(a, 256, b, 0, 256);
    final long hab = h.hash(a);
    final long hba = h.hash(b);
    assertEquals(hab, hba);
  }

  public void testZeros() {
    final Random r = new Random();
    final byte[] a = new byte[128];
    for (int m = 0; m < 10; ++m) {
      r.nextBytes(a);
      for (int k = 0; k < 64; ++k) {
        a[k] = (byte) (a[k + 64] + 64);
      }
      assertEquals(0, getHashFunction().hash(a));
    }
  }

  public void testKnownCollision() {
    final HashFunction h = getHashFunction();
    assertEquals(0x3C94B69E8C826B00L, h.hash(new byte[] {0x15, 0x72, (byte) 0xA8, 0x11, (byte) 0xB5}));
    assertEquals(0x3C94B69E8C826B00L, h.hash(new byte[] {0x70, 0x7F, 0x04, 0x00, (byte) 0xD4}));
  }

  /** Randomly generated arrays used to compute <code>hash</code> codes */
  private static final long[] HASH_BLOCKS;
  static {
    HASH_BLOCKS = new long[256];
    final Random r = new Random(1L); // use same seed for deterministic behavior
    for (int i = 0; i < 256; ++i) {
      HASH_BLOCKS[i] = r.nextLong();
    }
  }

  public void testPolynomialTheory() {
    final HashFunction h = getHashFunction();
    final Random r = new Random();
    final byte[] a = new byte[15];
    for (int m = 0; m < 10; ++m) {
      r.nextBytes(a);
      final long h0 = h.hash(a);
      long s = 0;
      for (int k = 0; k < a.length; ++k) {
        s ^= Long.rotateLeft(HASH_BLOCKS[(a[k] + k) & 0xFF], a.length - k - 1);
      }
      assertEquals(h0, s);
    }
  }
}
