package irvine.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * A 64-bit hash function.
 * @author Sean A. Irvine
 */
public interface HashFunction {

  /**
   * Returns a 64 bit hash of the given bytes.
   *
   * @param in bytes to checksum
   * @return a hash
   * @exception NullPointerException if <code>in</code> is null.
   */
  long hash(final byte[] in);

  /**
   * Returns a 64 bit hash of the given string.
   *
   * @param in string to checksum (should not be null)
   * @return a hash
   * @exception NullPointerException if <code>in</code> is null.
   */
  long hash(final CharSequence in);

  /**
   * Returns a 64 bit hash of the given stream.
   *
   * @param in input stream to checksum (should not be null)
   * @return a hash
   * @exception IOException if an I/O error occurs
   * @exception NullPointerException if <code>in</code> is null.
   */
  long hash(final InputStream in) throws IOException;
}
