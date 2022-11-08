package irvine.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(AlphabetFilterTest.class);
    suite.addTestSuite(AlphabeticalFilterTest.class);
    suite.addTestSuite(ContainsFilter.class);
    suite.addTestSuite(DecreasingFilterTest.class);
    suite.addTestSuite(DiplogramFilterTest.class);
    suite.addTestSuite(IncreasingFilterTest.class);
    suite.addTestSuite(LengthFilterTest.class);
    suite.addTestSuite(MaxLengthFilterTest.class);
    suite.addTestSuite(MinLengthFilterTest.class);
    suite.addTestSuite(PalindromeFilterTest.class);
    suite.addTestSuite(ReverseAlphabeticalFilterTest.class);
    suite.addTestSuite(TautonymFilterTest.class);
    return suite;
  }
}
