package irvine.jilt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import irvine.util.IOUtils;

/**
 * Load a dictionary.
 * @author Sean A. Irvine
 */
public final class Dictionary {

  private Dictionary() {
  }

  private static final String DEFAULT_DICT = "/irvine/resources/dict.txt.gz";

  /**
   * Get a dictionary as a reader.  There is no guarantee on the order in which
   * words will be present, or the casing of the words.
   * If the <code>dictionarySource</code> is <code>null</code>,
   * then the default dictionary is loaded; otherwise if <code>dictionarySource</code>
   * is <code>"-"</code>, then the dictionary is read from standard input; otherwise
   * the dictionary is loaded from the specified file.
   * @param dictionarySource dictionary to load
   * @return reader supplying words
   */
  public static BufferedReader getDictionaryReader(final String dictionarySource) {
    try {
      if (dictionarySource == null) {
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(Objects.requireNonNull(Dictionary.class.getResourceAsStream(DEFAULT_DICT)))));
      } else {
        return IOUtils.getReader(dictionarySource);
      }
    } catch (final IOException e) {
      throw new RuntimeException("Problem accessing dictionary", e);
    }
  }
}
