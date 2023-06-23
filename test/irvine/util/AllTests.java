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
    suite.addTestSuite(DateTest.class);
    suite.addTestSuite(DynamicArrayTest.class);
    suite.addTestSuite(DynamicLongArrayTest.class);
    suite.addTestSuite(CollectionUtilsTest.class);
    suite.addTestSuite(IntegerUtilsTest.class);
    suite.addTestSuite(IOUtilsTest.class);
    suite.addTestSuite(IrvineHashFunctionTest.class);
    suite.addTestSuite(LimitedLengthPriorityQueueTest.class);
    suite.addTestSuite(LongDynamicArrayTest.class);
    suite.addTestSuite(LongDynamicLongArrayTest.class);
    suite.addTestSuite(LongUtilsTest.class);
    suite.addTestSuite(PermutationTest.class);
    suite.addTestSuite(ShuffleTest.class);
    suite.addTestSuite(SortTest.class);
    suite.addTestSuite(StringUtilsTest.class);
    suite.addTestSuite(TrieTest.class);
    suite.addTestSuite(WrappingStringBufferTest.class);
    return suite;
  }
}
