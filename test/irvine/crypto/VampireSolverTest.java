package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import irvine.StandardIoTestCase;
import irvine.TestUtils;
import irvine.entropy.FourGramAlphabetModel;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class VampireSolverTest extends StandardIoTestCase {

  public void testCryptogramSetting() throws IOException {
    assertEquals("ABZ000A MV000", VampireSolver.cleanCryptogram(new BufferedReader(new StringReader("abZ019a mv.4$31\\? ")), false, false));
    assertEquals("ABZ000A MV\u0000000", VampireSolver.cleanCryptogram(new BufferedReader(new StringReader("abZ019a mv.4$31\\? ")), true, false));
  }

  public void testVampire() throws IOException {
    final VampireSolver vampire = new VampireSolver(System.out, FourGramAlphabetModel.loadModel(), null, true);
    try (final BufferedReader reader = new BufferedReader(new StringReader("We live in a wonderful world that is full of beauty, charm and adventure. There is no end to the adventures that we can have if only we seek them with our eyes open."))) {
      vampire.setCryptogram(reader, false);
    }
    vampire.setMaximumHypothesisCount(100);
    vampire.solve();
    final String s = getOut();
    //mOldOut.println(s);
    TestUtils.containsAll(s,
      "Starting percolate for B",
      "278.532 we live in a wonderful world that is full of beauty charm and adventure there is no end to the adventures that we",
      "Ciphertext: ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      "Plaintext:  abcdef.hi.klmnop.rstuvw.y.",
      "Keys:");
  }
}
