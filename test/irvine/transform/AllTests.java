package irvine.transform;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(LoopsTransformTest.class);
    suite.addTestSuite(LowercaseTransformTest.class);
    suite.addTestSuite(ReverseTransformTest.class);
    suite.addTestSuite(ScrabbleTransformTest.class);
    suite.addTestSuite(SortTransformTest.class);
    suite.addTestSuite(SumTransformTest.class);
    suite.addTestSuite(TelephoneSumTransformTest.class);
    suite.addTestSuite(TelephoneTransformTest.class);
    suite.addTestSuite(TitlecaseTransformTest.class);
    suite.addTestSuite(UppercaseTransformTest.class);
    return suite;
  }
}
