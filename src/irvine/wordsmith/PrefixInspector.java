package irvine.wordsmith;

import java.io.IOException;
import java.util.Set;

import irvine.jilt.Dictionary;
import irvine.util.Casing;

/**
 * Check if there is a prefix that can be added to every word.
 * @author Sean A. Irvine
 */
public class PrefixInspector implements Inspector {

  private boolean isPrefix(final Set<String> dict, final String prefix, final String... words) {
    for (final String w : words) {
      final String t = prefix + w;
      if (!dict.contains(t)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    try {
      final Set<String> dict = Dictionary.getWordSet(Dictionary.getDictionaryReader(null), 1, Integer.MAX_VALUE, Casing.UPPER);
      final StringBuilder sb = new StringBuilder();
      for (final String prefix : dict) {
        if (isPrefix(dict, prefix, words)) {
          if (sb.length() > 0) {
            sb.append('\n');
          }
          sb.append("Every word can be prefixed with: ").append(prefix);
        }
      }
      return sb.length() == 0 ? null : sb.toString();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
