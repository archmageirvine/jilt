package irvine.entropy;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class FourGramAlphabetModelTest extends TestCase {

  public void testBuild() throws Exception {
    final FourGramAlphabetModel m = new FourGramAlphabetModel("abcdefghijklmnopqrstuvwxyz ");
    try (final ByteArrayInputStream bis = new ByteArrayInputStream("dog".getBytes())) {
      m.add(bis);
      assertEquals(0.5596157879354227, m.entropy("d"), 1E-6);
      assertEquals(1.252762968495368, m.entropy("dog"), 1E-6);
      assertEquals(156.4425035662359, m.entropy("the quick brown fox jumped over the lazy dog."), 1E-6);
    }
  }
}

