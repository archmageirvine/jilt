package irvine.entropy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class WordGramModelTest extends TestCase {

  public void test() throws IOException {
    final WordGramModel model = new WordGramModel(1);
    model.add(new ByteArrayInputStream("hello this is a test hello".getBytes(StandardCharsets.US_ASCII)));
    assertEquals(1.2527, model.entropy("hello"), 1e-3);
    assertEquals(3.19867, model.entropy("this hello"), 1e-3);
  }
}
