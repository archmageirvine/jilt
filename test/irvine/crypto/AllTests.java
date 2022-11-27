package irvine.crypto;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(AutospaceTest.class);
    suite.addTestSuite(CrackHomophonicTest.class);
    suite.addTestSuite(HamadPlayfairTest.class);
    suite.addTestSuite(NodeTest.class);
    suite.addTestSuite(PlayfairSolverTest.class);
    suite.addTestSuite(PlayfairTest.class);
    suite.addTestSuite(ShrivastavaChouhanDhawanTest.class);
    suite.addTestSuite(SmallTranspositionTest.class);
    suite.addTestSuite(VampireSolverTest.class);
    return suite;
  }

  public static void main(final String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
