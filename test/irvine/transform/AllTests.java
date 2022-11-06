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
    suite.addTestSuite(TitlecaseTransformTest.class);
    suite.addTestSuite(UppercaseTransformTest.class);
    return suite;
  }
}
