package irvine.crypto;

import java.io.File;

import irvine.StandardIoTestCase;
import irvine.TestUtils;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class SmallTranspositionTest extends StandardIoTestCase {

  private static final String CIPHERTEX = "HITISSTEATOSTRFNSAOSPTIINSO";

  public void test() throws Exception {
    final File cipherfile = TestUtils.stringToFile(CIPHERTEX, File.createTempFile("transpo", ".txt"));
    try {
      SmallTransposition.main(cipherfile.getPath());
      final String s = getOut();
      //mOldOut.println(s);
      TestUtils.containsAll(s,
        "Considering order 1",
        "Considering order 3",
        "Considering order 9",
        "53 THIS ISATEST OF TRANSPOSITIONS");
    } finally {
      assertTrue(cipherfile.delete());
    }
  }
}
