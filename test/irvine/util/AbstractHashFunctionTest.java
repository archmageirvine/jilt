package irvine.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Test the hash functions
 *
 * @author Sean A. Irvine
 */
public abstract class AbstractHashFunctionTest extends TestCase {

  private static final String[] TEST_CASES = {
      "",
      "a",
      "c",
      "d",
      "e",
      "f",
      "g",
      "h",
      "i",
      "j",
      "k",
      "l",
      "m",
      "n",
      "o",
      "p",
      "q",
      "r",
      "s",
      "t",
      "u",
      "v",
      "w",
      "x",
      "y",
      "z",
      " ",
      "\n",
      "\t",
      ".",
      "~",
      "the",
      "12243",
      "sean",
      "the lazy brown dog",
      "Hamilton",
      "quark-gluon plasma",
      "IBM",
      "some random gibberish",
      "<<<>>>",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      "abcdefghijklmnopqrstuvwxyz",
      "  ",
      "\u38DD",
      "\u5040\u7800",
      "kldsfjiwefiewufbhwyeurvbewhofewufyuwgefyrweyugfgwyeufyweyur4587r43ryewfuwegfyuweygfgyeufgyebcbegfbuirerti435rt6ewyfdewhyfyguewgfyiehyvhunreghjvbrtjihiojythjuerjnfjiqwehg32746ge 674632 rt63hcr6tc t66 b6bn 674326 46h 632h6fd6twfewygfewfyug34utyrevgfb v345t2uyi3897y1247893785ty4gbvdeb hu iogoiret09309r390-rwegew[yge wehp vwphfewh hqwurugewrfg wrtwehtgw bbasfg3ewgfvkdsgfiuwehg rhjtrhjaspof yewgf lewtrlywf bd;rkthjerwphbnd;'sagbjfpoiywen e;en qiugf sbioebwtgr fhblsxb ldskvbslfbkwebf subt fph74f hesoi;tn efwe;4nye8325nyr wqjv940u fha;rfhupqwfsdphgfna ihwg b'xdstuwehjfqw thq'ureds'fjuwehtf s fhwehgfoiwuepguprehpj gbakjstghuyre ihowhig j[qwytpe tyhun",
    };

  public abstract HashFunction getHashFunction();

  private static final int ITERATIONS = 100000;

  private static int getIterations() {
    final String its = System.getProperty("hashiterations");
    if (its == null) {
      return ITERATIONS;
    } else {
      return Integer.parseInt(its);
    }
  }

  public void testNulls() throws IOException {
    final HashFunction h = getHashFunction();
    try {
      h.hash((byte[]) null);
      fail("accepted null");
    } catch (final NullPointerException e) {
      // ok
    }
    try {
      h.hash((String) null);
      fail("accepted null");
    } catch (final NullPointerException e) {
      // ok
    }
    try {
      h.hash((InputStream) null);
      fail("accepted null");
    } catch (final NullPointerException e) {
      // ok
    }
  }

  public void testUnique() {
    final HashFunction h = getHashFunction();
    final HashSet<Long> s = new HashSet<>();
    for (final String testCase : TEST_CASES) {
      final Long v = h.hash(testCase);
      if (s.contains(v)) {
        fail("Hash function broken");
      } else {
        s.add(v);
      }
    }
  }

  public void testConsistency0() {
    final HashFunction h = getHashFunction();
    final String text = "There was an old man who smoked. Gandalf was his name";
    final long v = h.hash(text);
    for (int i = 0; i < 10; ++i) {
      assertEquals(v, h.hash(text));
    }
    assertEquals(v, h.hash(text.getBytes()));
    try {
      final InputStream in = new ByteArrayInputStream(text.getBytes());
      assertEquals(v, h.hash(in));
      in.close();
    } catch (final IOException e) {
      fail("IO problem");
    }
  }

  public void testConsistency1() throws IOException {
    final HashFunction h = getHashFunction();
    final Random r = new Random(42);
    long s0 = 0L;
    long s1 = 0L;
    long s2 = 0L;
    final byte[] t = new byte[100];
    final int it = getIterations() / 10;
    for (int i = 0; i < it; ++i) {
      r.nextBytes(t);
      s0 += h.hash(t);
      s1 += h.hash(new String(t, StandardCharsets.ISO_8859_1));
      s2 += h.hash(new ByteArrayInputStream(t));
    }
    assertEquals(s0, s1);
    assertEquals(s0, s2);
  }
}
