package irvine.language;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for all tests in this directory.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(AnagramTest.class);
    suite.addTestSuite(CaesarTest.class);
    suite.addTestSuite(ChainTest.class);
    suite.addTestSuite(MultiwordAnagramTest.class);
    return suite;
  }
}

