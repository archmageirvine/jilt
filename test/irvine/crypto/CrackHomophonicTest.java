package irvine.crypto;

import java.io.IOException;

import irvine.StandardIoTestCase;
import irvine.TestUtils;
import irvine.entropy.FourGramAlphabetModel;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class CrackHomophonicTest extends StandardIoTestCase {

  // This is actually a piece of text from The Hobbit, but it will not be solved in the test
  private static final int[] CIPHER = {
    41, 57, 2, 36, 62, 48, 21, 40, 53, 84, 37, 17, 32, 71, 64, 92, 57, 15, 86, 36, 23, 72, 20, 50, 41, 93, 24, 14, 5, 37, 61, 8, 9, 40, 82, 55, 62, 82, 3, 57, 0, 76, 82, 97, 15, 44, 72, 85, 97, 94, 18, 85, 34, 64, 48, 21, 29, 41, 48, 50, 23, 15, 94, 39, 89, 36, 85, 38, 17, 28, 55, 13, 80, 65, 30, 95, 63, 70, 51, 80, 7, 57, 14, 6, 58, 65, 63, 99, 97, 75, 52, 27, 48, 47, 55, 59, 70, 97, 19, 89, 2, 13, 69, 98, 8, 6, 71, 19, 75, 0, 58, 13, 97, 33, 59, 50, 17, 94, 39, 81, 33, 57, 61, 89, 38, 41, 53, 31, 43, 55, 44, 83, 83, 59, 76, 41, 82, 15, 61, 95, 56, 63, 53, 63, 71, 89, 65, 25, 3, 86, 39, 83, 94, 2, 79, 1, 38, 64, 9, 9, 44, 81, 38, 59, 47, 27, 4, 54, 16, 87, 37, 5, 88, 51, 24, 6, 58, 80, 11, 59, 51, 29, 62, 74, 89, 41, 85, 37, 4, 15, 3, 67, 18, 69, 29, 22, 12, 82, 47, 97, 70, 62, 92, 55, 16, 16, 64, 62, 70, 47, 39, 46, 17, 2, 66, 60, 73, 81, 33, 61, 48, 17, 66, 6, 40, 58, 86, 27, 14, 32, 72, 28, 19, 54, 95, 39, 84, 36, 4, 77, 35, 43, 58, 98, 97, 17, 47, 48, 62, 95, 9, 74, 3, 76, 77, 46, 55, 61, 8, 43, 58, 86, 38, 24, 21, 96, 5, 12, 81, 51, 44, 14, 13, 48, 21, 85, 37, 24, 14, 59, 65, 72, 59, 67, 27, 57, 22, 13, 64, 58, 84, 60, 6, 86, 92, 9, 23, 76, 37, 5, 66, 17, 15, 33, 3, 50, 47, 49, 41, 46, 27, 2, 83, 91, 56, 53, 22, 50, 4, 93, 26, 74, 98, 11, 64, 52, 30, 63, 70, 85, 2, 8, 47, 27, 86, 91, 54, 56, 23, 47, 95, 42, 89, 37, 63, 92, 85, 76, 51, 61, 46, 21, 95, 40, 86, 33, 67, 4, 54, 20, 47, 50, 25, 14, 94, 1, 49, 48, 77, 2, 55, 14, 29, 50, 60, 62, 70, 80, 82, 40, 47, 24, 16, 6, 56, 14, 12, 0, 72, 67, 28, 88, 25, 14, 67, 73, 63, 93, 40, 16, 28, 15, 95, 42, 88, 35, 66, 59, 49, 40, 79, 38, 23, 14, 10, 37, 4, 40, 73, 75, 3, 54, 15, 48, 59, 83, 77, 2, 54, 14, 47, 64, 88, 80, 61, 29, 66, 26, 31, 75, 30, 63, 70, 35, 5, 86, 80, 3, 58, 13, 11, 61, 1, 89, 76, 86, 37, 27, 38, 59, 9, 8, 41, 85, 94, 2, 78, 29, 59, 56, 14, 62, 29, 93, 39, 76, 39, 87, 60, 72, 79, 89, 35, 25, 89, 91, 58, 56, 26, 50, 95, 61, 90, 57, 14, 59, 54, 4, 54, 16, 61, 58, 31, 62, 43, 58, 31, 30, 4, 44, 74, 49, 98, 8, 91, 84, 55, 63, 83, 68, 92, 39, 87, 28, 76, 86, 72, 3, 42, 32, 35, 87, 40, 55, 88, 61, 88, 34, 22, 79, 44, 13, 17, 63, 30, 88, 35, 25, 37, 42, 49, 48, 88, 35, 26, 35, 44, 48, 47, 2, 75, 5, 49, 50, 81, 35, 24, 66, 18, 65, 66, 50, 24, 30, 65, 70, 52, 5, 57, 98, 52, 42, 50, 28, 75, 69, 65, 92, 54, 13, 11, 0, 48, 49, 20, 15, 40, 83, 4, 57, 16, 52, 4, 55, 98, 49, 40, 85, 85, 48, 23, 73, 64, 90, 53, 13, 75, 15, 64, 62, 73, 60, 66, 19, 57, 26, 16, 63, 91, 86, 64, 30, 41, 81, 30, 41, 72, 76, 89, 64, 53, 60, 55, 21, 75, 44, 14, 25, 2, 53, 16, 84, 35, 24, 53, 62, 53, 3, 53, 62, 86, 36, 25, 69, 58, 59, 31, 64, 42, 54, 31, 92, 66, 75, 81, 7, 40, 72, 79, 30, 62, 71, 83, 38, 21, 35, 64, 8, 9, 42, 88, 8, 25, 13, 73, 59, 60, 52, 80, 9, 1, 87, 35, 69, 60, 60, 51, 76, 10, 19, 48, 48, 3, 74, 77, 66, 3, 53, 87, 71, 39, 22, 79, 48, 59, 81, 78, 60, 29, 87, 34, 22, 78, 26, 95, 6, 74, 16, 74, 63, 8, 20, 80, 34, 26, 38, 3, 15, 94, 35, 60, 47, 18, 74, 59, 64, 52, 77, 15, 24, 93, 59, 81, 18, 13, 85, 65, 10, 47, 64, 82, 35, 17, 77, 46, 44, 85, 10, 35, 25, 53, 79, 13, 44, 58, 42, 54, 32, 70, 63, 64, 51, 80, 3, 48, 49, 95, 19, 70, 27, 61, 56, 84, 33, 24, 78, 0, 52, 28, 29, 48, 60, 65, 69, 0, 58, 13, 44, 57, 13, 18, 17, 14, 64, 57, 87, 37, 22, 79, 5, 51, 19, 67, 4, 80, 79, 0, 31, 28, 86, 37, 17, 9, 20, 79, 83, 70, 62, 65, 52, 80, 94, 22, 70, 25, 2, 47, 47, 61, 54, 84, 34, 24, 49, 17, 29, 84, 34, 2, 53, 16, 76, 42, 16, 21, 32, 62, 43, 54, 31, 42, 56, 30, 65, 69, 88, 36, 28, 79, 25, 94, 24, 72, 27, 88, 35, 20, 59, 55, 47, 98, 60, 56, 28, 75, 88, 63, 38, 3, 93, 23, 94, 40, 53, 16, 60, 95, 80, 16, 28, 24, 66, 75, 17, 85, 69, 61, 92, 57, 16, 95, 40, 54, 13, 59, 95, 79, 48, 60, 60, 46, 39, 54, 31, 63, 93, 23, 72, 33, 39, 79, 31, 5, 74, 15, 20, 53, 2, 58, 14, 52, 27, 3, 15, 65, 95, 79, 9, 20, 98, 65, 53, 16, 75, 49, 59, 66, 44, 57, 31, 13, 61, 94, 58, 86, 63, 81, 34, 18, 71, 43, 93, 19, 74
  };

  // Not very realistic it keeps minimal solutions
  public void testUgly() throws IOException {
    final FourGramAlphabetModel model = FourGramAlphabetModel.loadModel();
    final CrackHomophonic cracker = new CrackHomophonic(model, CrackHomophonic.DEFAULT_DISTRIBUTION, 2);
    cracker.setSeed(42);
    cracker.solveByExchange(CIPHER, 2);
    final String s = getOut();
    //mOldOut.println(s);
    TestUtils.containsAll(s,
      "3799.9740 16 ENUSERIENTUADDAFNALSOSNGETOLAUANIEPIEPANERPEAOSGENOGHARISERGOANERSGRATID",
      "Doing 99 cycle=1");
  }
}
