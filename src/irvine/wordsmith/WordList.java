package irvine.wordsmith;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import irvine.util.IOUtils;

/**
 * Hold a word list and potential associated data.
 * @author Sean A. Irvine
 */
public class WordList extends LinkedHashMap<String, List<String>> {

  private static final List<String> EMPTY_LIST = Collections.emptyList();

  private final String mName;
  private String mDescription = null;

  private static String format(final String s, final boolean clean) {
    if (clean) {
      final StringBuilder sb = new StringBuilder();
      for (int k = 0; k < s.length(); ++k) {
        final char c = s.charAt(k);
        if (c >= 'A' && c <= 'Z') {
          sb.append(c);
        }
      }
      return sb.toString();
    } else {
      return s;
    }
  }

  WordList(final String file, final boolean clean) {
    mName = file;
    try (final BufferedReader r = IOUtils.getReader(file)) {
      String line;
      while ((line = r.readLine()) != null) {
        if (line.length() > 0) {
          if (line.charAt(0) == '#') {
            if (line.startsWith("#@")) {
              mDescription = line.substring(2);
            }
          } else {
            final String[] parts = line.split(",");
            final String key = format(parts[0].toUpperCase(Locale.getDefault()), clean);
            if (parts.length == 1) {
              put(key, EMPTY_LIST);
            } else {
              final List<String> other = Arrays.asList(parts).subList(1, parts.length);
              put(key, other);
            }
          }
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  WordList(final String file) {
    this(file, false);
  }

  String getDescription() {
    return mDescription != null ? mDescription : "in " + mName;
  }
}
