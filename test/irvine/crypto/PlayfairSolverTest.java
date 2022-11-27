package irvine.crypto;

import java.io.IOException;

import irvine.StandardIoTestCase;
import irvine.TestUtils;
import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class PlayfairSolverTest extends StandardIoTestCase {

  public void testReverse() {
    assertEquals("", new String(PlayfairSolver.reverse(new char[0])));
    assertEquals("ba", new String(PlayfairSolver.reverse(new char[] {'a', 'b'})));
    assertEquals("cba", new String(PlayfairSolver.reverse(new char[] {'a', 'b', 'c'})));
  }

  public void testFlips() {
    final char[] a = "abcdefghiklmnopqrstuvwxyz".toCharArray();
    assertEquals("vwxyzqrstulmnopfghikabcde", new String(PlayfairSolver.reflectVertical(a, 5, 5)));
    assertEquals("edcbakihgfponmlutsrqzyxwv", new String(PlayfairSolver.reflectHorizontal(a, 5, 5)));
  }

  public void testSwapRows() {
    final PlayfairSolver cp = new PlayfairSolver(System.out, null, Playmate.FIVE_ALPHABET_NO_J, 1, 5, 5);
    final char[] a = "abcdefghiklmnopqrstuvwxyz".toCharArray();
    cp.swapRows(a, 0, 1);
    assertEquals("fghikabcdelmnopqrstuvwxyz", new String(a));
    cp.swapRows(a, 0, 1);
    assertEquals("abcdefghiklmnopqrstuvwxyz", new String(a));
  }

  public void testSwapCols() {
    final PlayfairSolver cp = new PlayfairSolver(System.out, null, Playmate.FIVE_ALPHABET_NO_J, 1, 5, 5);
    final char[] a = "abcdefghiklmnopqrstuvwxyz".toCharArray();
    cp.swapCols(a, 0, 1);
    assertEquals("bacdegfhikmlnoprqstuwvxyz", new String(a));
    cp.swapCols(a, 0, 1);
    assertEquals("abcdefghiklmnopqrstuvwxyz", new String(a));
  }

  public void testReal() throws IOException {
    final Entropy model = FourGramAlphabetModel.loadModel();
    final int hypotheses = 5;
    final PlayfairSolver cracker = new PlayfairSolver(System.out, model, Playmate.FIVE_ALPHABET_NO_J, hypotheses, 5, 5);
    cracker.setSeed(42);
    final String ciphertext = "CTLGQISESKENCRGDFMCGOYPAZFNSHPUGQABKFPSRSGPO";
    cracker.percolate(ciphertext, 5, null);
    final String s = getOut();
    //mOldOut.println(s);
    TestUtils.containsAll(s,
      "171.531 (0) [RPNTXZVWOFSBMYDQLKECUIAGH] EXEILUYQMQKTQXHYWDEHTONIFORMIXHAKUMLVXZUYUTV",
      "Doing iteration: 5/5");
  }
}
