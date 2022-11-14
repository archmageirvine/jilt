package irvine.entropy;

import java.io.File;
import java.io.FileInputStream;

import irvine.StandardIoTestCase;
import irvine.TestUtils;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 */
public class UniwordModelTest extends StandardIoTestCase {

  private static final String LS = System.lineSeparator();
  private static final String WORD_A = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
  private static final String WORD_B = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
  private static final String INPUT = "5 " + WORD_A + WORD_B + LS + "5 " + WORD_B + WORD_A + LS;

  public void testDefaultModel() throws Exception {
    final UniwordModel m = UniwordModel.defaultEnglishModel();
    final double e = m.entropy("the quick brown fox jumped over the lazy dog.");
    assertTrue(e < 500.0);
    assertEquals(e, m.entropy("THE QUICK BROWN FOX  JUMPED OVER THE \t LAZY DOG."), 1E-8);
  }

  public void testBuild() throws Exception {
    final File t = TestUtils.stringToFile("5 dog", File.createTempFile("uniword", "model"));
    try {
      final UniwordModel m = new UniwordModel(t);
      assertEquals(1.791759469228055, m.entropy("d"), 1E-6);
      assertEquals(0.18232155679395468, m.entropy("dog"), 1E-6);
      assertEquals(14.516397310618393, m.entropy("the quick brown fox jumped over the lazy dog."), 1E-6);
      assertEquals("There were 0 collisions (ideally 0)" + LS + "maxcount=5 logScalingFactor=0.006311521225231766" + LS, getOut());
      reset();
      final String mx = t.getPath() + ".model";
      try {
        UniwordModel.main(new String[]{"--build", mx, t.getPath()});
        try (final FileInputStream fis = new FileInputStream(mx)) {
          final UniwordModel m2 = new UniwordModel(fis);
          assertEquals(1.791759469228055, m2.entropy("d"), 1E-6);
          assertEquals(0.18232155679395468, m2.entropy("dog"), 1E-6);
          assertEquals(14.516397310618393, m2.entropy("the quick brown fox jumped over the lazy dog."), 1E-6);
        }
      } finally {
        assertTrue(new File(mx).delete());
      }
      assertEquals("There were 0 collisions (ideally 0)" + LS + "maxcount=5 logScalingFactor=0.006311521225231766" + LS + "Model saved." + LS, getOut());
    } finally {
      assertTrue(t.delete());
    }
  }

  public void testCollision() throws Exception {
    final File t = TestUtils.stringToFile(INPUT, File.createTempFile("uniword", "model"));
    try {
      new UniwordModel(t);
      assertEquals("There were 1 collisions (ideally 0)" + LS + "maxcount=5 logScalingFactor=0.006311521225231766" + LS, getOut());
    } finally {
      assertTrue(t.delete());
    }
  }
}

