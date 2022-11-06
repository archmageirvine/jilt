package irvine.transform;

import irvine.filter.AlphabeticalFilterTest;
import irvine.filter.DecreasingFilterTest;
import irvine.filter.IncreasingFilterTest;
import irvine.filter.LengthFilterTest;
import irvine.filter.MaxLengthFilterTest;
import irvine.filter.MinLengthFilterTest;
import irvine.filter.PalindromeFilterTest;
import irvine.filter.ReverseAlphabeticalFilterTest;
import irvine.filter.TautonymFilterTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Links all the tests in this package.
 * @author Sean A. Irvine
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(LowercaseTransformTest.class);
    suite.addTestSuite(TitlecaseTransformTest.class);
    suite.addTestSuite(UppercaseTransformTest.class);
    return suite;
  }
}
