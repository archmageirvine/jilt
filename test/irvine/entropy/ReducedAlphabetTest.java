package irvine.entropy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class ReducedAlphabetTest extends TestCase {

  public void test() throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream("ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\rabcdefghijklmnopqrstuvwxyz\t0123456789`~!@#$%^&*()_-+=[]{};:'\"<>,./?\\|  ".getBytes(StandardCharsets.US_ASCII));
         final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      ReducedAlphabet.stream(is, os);
      assertEquals("ABCDEFGHIJKLMMOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ 0000000000'|.||||||||| ||||||..''||.. .|| ", os.toString());
    }
  }
}
