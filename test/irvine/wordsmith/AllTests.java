package irvine.wordsmith;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(AlphabeticalInspectorTest.class);
    suite.addTestSuite(ConsecutiveLettersInspectorTest.class);
    suite.addTestSuite(ConstantInspectorTest.class);
    suite.addTestSuite(DoubledLetterInspectorTest.class);
    suite.addTestSuite(LengthInspectorTest.class);
    suite.addTestSuite(ReverseAlphabeticalInspectorTest.class);
    return suite;
  }
}
