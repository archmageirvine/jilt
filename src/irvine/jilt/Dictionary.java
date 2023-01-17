package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import irvine.util.Casing;
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

  /**
   * Retrieve all the words with specified length bounds.
   * @param reader source of words
   * @param minLength minimum length
   * @param maxLength maximum length
   * @param casing case handling
   * @return set of words
   * @throws IOException if an I/O problem occurs
   */
  public static Set<String> getWordSet(final BufferedReader reader, final int minLength, final int maxLength, final Casing casing) throws IOException {
    final HashSet<String> res = new HashSet<>();
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.length() >= minLength && line.length() <= maxLength) {
        if (casing == Casing.UPPER) {
          res.add(line.toUpperCase(Locale.getDefault()));
        } else if (casing == Casing.LOWER) {
          res.add(line.toLowerCase(Locale.getDefault()));
        } else {
          res.add(line);
        }
      }
    }
    return res;
  }

  /**
   * Retrieve all the words with specified length bounds.
   * @param reader source of words
   * @param minLength minimum length
   * @param maxLength maximum length
   * @return set of words
   * @throws IOException if an I/O problem occurs
   */
  public static Set<String> getWordSet(final BufferedReader reader, final int minLength, final int maxLength) throws IOException {
    return getWordSet(reader, minLength, maxLength, Casing.NONE);
  }
}
