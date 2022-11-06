package irvine.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Test if word is in a given word list.
 * @author Sean A. Irvine
 */
public class SetFilter implements Filter {

  private final Set<String> mDict = new HashSet<>();

  /**
   * Construct a set filter based on words in the given reader.
   * @param reader source of words
   * @throws IOException if an I/O problem occurs
   */
  public SetFilter(final BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      mDict.add(line);
    }
  }

  @Override
  public boolean is(final String word) {
    return mDict.contains(word);
  }
}
