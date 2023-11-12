package irvine.wordsmith;

import java.util.ArrayList;

import irvine.jilt.Dictionary;

/**
 * Check if they remain words when adding a single letter.
 * @author Sean A. Irvine
 */
public class AddSingleLetterInspector implements Inspector {

  protected String is(final char c, final String word) {
    for (int k = 0; k <= word.length(); ++k) {
      final String w = word.substring(0, k) + c + word.substring(k);
      if (Dictionary.getDefaultDictionary().contains(w)) {
        return w;
      }
    }
    return null;
  }

  private void checkConstantLetter(final char c, final String[] words, final StringBuilder sb) {
    final ArrayList<String> s = new ArrayList<>();
    for (final String w : words) {
      final String replace = is(c, w);
      if (replace == null) {
        return;
      }
      s.add(replace);
    }
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append("The letter ").append(c).append(" can be added to every word:\n").append(s);
  }

  private void checkIncrementingLetter(final char c, final String[] words, final StringBuilder sb) {
    if ((char) (c + words.length) > 'Z') {
      return;
    }
    char d = c;
    final ArrayList<String> s = new ArrayList<>();
    for (final String w : words) {
      final String replace = is(d, w);
      if (replace == null) {
        return;
      }
      s.add(replace);
      ++d;
    }
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append("The letters ").append(c).append((char)(c + 1)).append((char)(c + 2)).append("... can be added:\n").append(s);
  }

  private void checkDecrementingLetter(final char c, final String[] words, final StringBuilder sb) {
    if ((char) (c - words.length) < 'A') {
      return;
    }
    char d = c;
    final ArrayList<String> s = new ArrayList<>();
    for (final String w : words) {
      final String replace = is(d, w);
      if (replace == null) {
        return;
      }
      s.add(replace);
      --d;
    }
    sb.append("The letters ").append(c).append((char)(c - 1)).append((char)(c - 2)).append("... can be added to every word:\n").append(s);
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 4) {
      return null;
    }
    final StringBuilder sb = new StringBuilder();
    for (char c = 'A'; c <= 'Z'; ++c) {
      checkConstantLetter(c, words, sb);
      checkIncrementingLetter(c, words, sb);
      checkDecrementingLetter(c, words, sb);
    }
    return sb.length() == 0 ? null : sb.toString();
  }
}
