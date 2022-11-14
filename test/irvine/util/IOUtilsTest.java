package irvine.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class IOUtilsTest extends TestCase {

  private static final String STRING = "yollywock";

  private static final byte[] BYTES = STRING.getBytes();

  private static final byte[] EMPTY = new byte[0];

  public void testEmpty() throws IOException {
    checkRealAll(EMPTY);
  }

  public void testString() throws IOException {
    checkRealAll(BYTES);
  }

  public void checkRealAll(final byte[] s) throws IOException {
    try (final InputStream in = new ByteArrayInputStream(s)) {
      final String res = IOUtils.readAll(in);
      final byte[] bres = res.getBytes();
      assertEquals(s.length, bres.length);
      for (int i = 0; i < s.length; ++i) {
        assertEquals(s[i], bres[i]);
      }
    }
  }

  public void testReadAllReader() throws IOException {
    try (final Reader in = new InputStreamReader(new ByteArrayInputStream(new byte[] {3, 4}))) {
      final String res = IOUtils.readAll(in);
      final byte[] bres = res.getBytes();
      assertEquals(2, bres.length);
      assertEquals(3, bres[0]);
      assertEquals(4, bres[1]);
    }
  }

  private static class MyStream extends InputStreamReader {
    MyStream(final InputStream is) {
      super(is);
    }
    @Override
    public int read(final char[] c) {
      return 0;
    }
  }

  public void testReadAllReaderWithZeroReturn() {
    try (final Reader in = new MyStream(new ByteArrayInputStream(new byte[2]))) {
      IOUtils.readAll(in);
      fail();
    } catch (final IOException e) {
      assertEquals("Read was 0 bytes", e.getMessage());
    }
  }
}
