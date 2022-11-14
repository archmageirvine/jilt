package irvine.entropy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 */
public class NGramModelTest extends TestCase {

  public void test() throws IOException {
    final NGramModel model = new NGramModel(4);
    model.add(new ByteArrayInputStream("abbbababababababbcbaaaaaaaaaccabababababababcccaaaaaaababababababababaaab".getBytes(StandardCharsets.US_ASCII)));
    assertEquals(0.6151856390902339, model.entropy("a"), 1e-4);
    assertEquals(1.008228227199841, model.entropy("b"), 1e-4);
    assertEquals(2.5123056239761152, model.entropy("c"), 1e-4);
    assertEquals(8.60813018640834, model.entropy("d"), 1e-4);
    assertEquals(2.497377443391865, model.entropy("aaaaa"), 1e-4);
    assertEquals(20.399327543834637, model.entropy("abbabbababababaaabbc"), 1e-4);
  }
}
