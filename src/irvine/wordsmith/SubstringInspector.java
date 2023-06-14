package irvine.wordsmith;

import java.util.ArrayList;

/**
 * Check if substrings of the words occur in a list.
 * @author Sean A. Irvine
 */
public class SubstringInspector implements Inspector {

  private final WordList mList;
  private final boolean mVerbose;

  SubstringInspector(final String file, final boolean verbose) {
    mList = new WordList(file);
    mVerbose = verbose;
  }

  private String search(final String word, final char[] buffer, final int posInWord, final int posInBuffer) {
    if (posInBuffer >= 3 && posInBuffer != word.length()) {
      final String key = new String(buffer, 0, posInBuffer);
      if (!word.equals(key) && mList.containsKey(key)) {
        return key;
      }
    }
    if (posInWord >= word.length()) {
      return null;
    }
    buffer[posInBuffer] = word.charAt(posInWord);
    final String t = search(word, buffer, posInWord + 1, posInBuffer + 1);
    if (t != null) {
      return t;
    }
    return search(word, buffer, posInWord + 1, posInBuffer);
  }

  private String search(final String word) {
    return search(word, new char[word.length()], 0, 0);
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 4) {
      return null;
    }
    for (final String w : words) {
      if (w.length() <= 3) {
        return null;
      }
      if (w.length() > 20) {
        return null;
      }
    }
    if (mVerbose) {
      System.out.println("Trying substring search on " + mList.getDescription());
    }
    // At least 4 words, all at least 4 letters
    final ArrayList<String> res = new ArrayList<>();
    for (final String w : words) {
      final String t = search(w);
      if (t == null) {
        return null;
      }
      res.add(t);
    }
    return "All words have a substring in " + mList.getDescription() + "\n" + res;
  }
}
