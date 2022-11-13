package irvine.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(CasingTest.class);
    suite.addTestSuite(CliFlagsTest.class);
    suite.addTestSuite(CollectionUtilsTest.class);
    suite.addTestSuite(IntegerUtilsTest.class);
    suite.addTestSuite(LongUtilsTest.class);
    suite.addTestSuite(PermutationTest.class);
    suite.addTestSuite(StringUtilsTest.class);
    suite.addTestSuite(WrappingStringBufferTest.class);
    return suite;
  }
}
