package irvine.associator;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class QueryStateTest extends TestCase {

  public void test() {
    final QueryState q = new QueryState(12, 42.0F);
    assertEquals(12, q.getWordIndex());
    assertEquals(42.0F, q.getWeight(), 1e-6);
  }
}
