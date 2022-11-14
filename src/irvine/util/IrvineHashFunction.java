package irvine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * A 64-bit hash function that overcomes some of limitations of the
 * java String <code>hashCode()</code> function.
 *
 * This function is known to have no collisions among byte arrays of
 * lengths 1 to 3 inclusive.  There are no collision among byte arrays
 * of length 4.  If A is any 256-byte string and B is
 * any other 256-byte string then hash(A+B)==hash(B+A). If A is any
 * 64-byte string and B is defined by <code>b[i] = (byte) (a[i] + 64)
 * </code>, then hash(B+A)=0.  Thus, 128-byte strings almost certainly
 * have a disproportionate number of zeros as hashes.
 *
 * Collisions of length 5 do exist, for example, <code>
 * {0x15, 0x72, 0xA8, 0x11, 0xB5} and  {0x70, 0x7F, 0x04, 0x00, 0xD4}
 * </code> both hash to <code>0x3C94B69E8C826B00</code>.
 *
 * @author Sean A. Irvine
 */
public final class IrvineHashFunction implements HashFunction {

  /** Randomly generated arrays used to compute <code>irvineHash</code> codes */
  private static final long[] HASH_BLOCKS;
  static {
    HASH_BLOCKS = new long[256];
    final Random r = new Random(1L); // use same seed for deterministic behavior
    for (int i = 0; i < 256; ++i) {
      HASH_BLOCKS[i] = r.nextLong();
    }
  }

  @Override
  public long hash(final byte[] in) {
    long r = 0L;
    for (int i = 0; i < in.length; ++i) {
      r = Long.rotateLeft(r, 1) ^ HASH_BLOCKS[(in[i] + i) & 0xFF];
    }
    return r;
  }

  @Override
  public long hash(final CharSequence in) {
    long r = 0L;
    for (int i = 0; i < in.length(); ++i) {
      r = Long.rotateLeft(r, 1) ^ HASH_BLOCKS[(in.charAt(i) + i) & 0xFF];
    }
    return r;
  }

  @Override
  public long hash(final InputStream in) throws IOException {
    long r = 0L;
    int b;
    int i = 0;
    while ((b = in.read()) != -1) {
      r = Long.rotateLeft(r, 1) ^ HASH_BLOCKS[(b + i++) & 0xFF];
    }
    return r;
  }
}
