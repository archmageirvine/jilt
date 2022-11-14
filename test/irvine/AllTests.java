package irvine;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTest(irvine.crypto.AllTests.suite());
    suite.addTest(irvine.entropy.AllTests.suite());
    suite.addTest(irvine.filter.AllTests.suite());
    suite.addTest(irvine.jilt.AllTests.suite());
    suite.addTest(irvine.language.AllTests.suite());
    suite.addTest(irvine.transform.AllTests.suite());
    suite.addTest(irvine.util.AllTests.suite());
    return suite;
  }
}
