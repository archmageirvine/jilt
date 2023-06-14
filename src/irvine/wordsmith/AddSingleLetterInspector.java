package irvine.wordsmith;

import irvine.jilt.Dictionary;

/**
 * Check if they remain words when adding a single letter.
 * @author Sean A. Irvine
 */
public class AddSingleLetterInspector implements Inspector {

  private boolean is(final char c, final String word) {
    for (int k = 0; k <= word.length(); ++k) {
      if (Dictionary.getDefaultDictionary().contains(word.substring(0, k) + c + word.substring(k))) {
        return true;
      }
    }
    return false;
  }

  private void checkConstantLetter(final char c, final String[] words, final StringBuilder sb) {
    for (final String w : words) {
      if (!is(c, w)) {
        return;
      }
    }
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append("The letter ").append(c).append(" can be added to every word");
  }

  private void checkIncrementingLetter(final char c, final String[] words, final StringBuilder sb) {
    if ((char) (c + words.length) > 'Z') {
      return;
    }
    char d = c;
    for (final String w : words) {
      if (!is(d, w)) {
        return;
      }
      ++d;
    }
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append("The letters ").append(c).append((char)(c + 1)).append((char)(c + 2)).append("... can be added");
  }

  private void checkDecrementingLetter(final char c, final String[] words, final StringBuilder sb) {
    if ((char) (c - words.length) < 'A') {
      return;
    }
    char d = c;
    for (final String w : words) {
      if (!is(d, w)) {
        return;
      }
      --d;
    }
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append("The letters ").append(c).append((char)(c - 1)).append((char)(c - 2)).append("... can be added to every word");
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
