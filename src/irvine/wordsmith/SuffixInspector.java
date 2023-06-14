package irvine.wordsmith;

import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if there is a suffix that can be added to every word.
 * @author Sean A. Irvine
 */
public class SuffixInspector implements Inspector {

  private boolean isSuffix(final Set<String> dict, final String suffix, final String... words) {
    for (final String w : words) {
      final String t = w + suffix;
      if (!dict.contains(t)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final StringBuilder sb = new StringBuilder();
    for (final String suffix : dict) {
      if (isSuffix(dict, suffix, words)) {
        if (sb.length() > 0) {
          sb.append('\n');
        }
        sb.append("Every word can be suffixed with: ").append(suffix);
      }
    }
    return sb.length() == 0 ? null : sb.toString();
  }
}
